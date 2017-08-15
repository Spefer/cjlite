package cjlite.web.core;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.app.Constants;
import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.FilePath;
import cjlite.utils.Lists;
import cjlite.utils.Maps;
import cjlite.utils.Strings;
import cjlite.web.ConfigFolder;
import cjlite.web.StaticFolderName;
import cjlite.web.handler.HandleResult;
import cjlite.web.helper.UrlHelper;
import cjlite.web.request.RequestDebugWrapper;
import cjlite.web.statics.StaticResourceManager;

public class StaticResourceManagerImpl implements StaticResourceManager {

	private static final Logger logger = Logger.thisClass();

	private static final String oneMoreFileMsg = "RequestPath[{0}] has one more files exist in folder, please try to rename it via fodler or something else....\n{1}, will use this one: {2}[webroot:{3}]";

	private final Config config;

	private List<String> driveStaticFolderPath;

	private List<ResourceFolder> resourceFolderList;

	private Map<ConfigFolder, List<ResourceFolder>> staticConfigRresourceFolderMap;

	/**
	 * Key: static folder name <br>
	 * Value: resource folder
	 */
	private Map<String, ResourceFolder> staticRresourceFolderMap;

	@SuppressWarnings("unused")
	private String webRootPath;

	/**
	 * Should the Accept-Ranges: bytes header be send with static resources?
	 */
	protected boolean useAcceptRanges = true;

	/**
	 * The input buffer size to use when serving resources.
	 */
	protected int input = 2048;

	/**
	 * The output buffer size to use when serving resources.
	 */
	protected int output = 2048;

	/**
	 * Full range marker.
	 */
	protected static final ArrayList<Range> FULL = new ArrayList<>();

	/**
	 * File encoding to be used when reading static files. If none is specified the platform default is used.
	 */
	protected String fileEncoding = null;

	/**
	 * MIME multipart separation string
	 */
	protected static final String mimeSeparation = "CATALINA_MIME_BOUNDARY";

	private ConfigFolder webRootFolder;

	private boolean debug = false;

	public StaticResourceManagerImpl(Config config) {
		this.config = config;
		this.webRootPath = this.config.getProperties("RootPath");
		this.parseConfigFolder();
		this.debug = Constants.Stage.Development.equalsIgnoreCase(this.config.getProperties("Stage", "Production"));
	}

	/**
	 * initial to parse static resources configuration folder
	 */
	private void parseConfigFolder() {
		driveStaticFolderPath = Lists.newArrayList();
		resourceFolderList = Lists.newArrayList();
		staticConfigRresourceFolderMap = Maps.newHashMap();
		staticRresourceFolderMap = Maps.newHashMap();

		Map<String, ConfigFolder> folderMap = config.getConfigFolders();

		for (String key : folderMap.keySet()) {
			String folderRootPath = config.getProperties(key);
			ConfigFolder cfolder = folderMap.get(key);
			if (!cfolder.isWebAccess()) {
				continue;
			}

			if (cfolder.isWebRootFolder()) {
				this.webRootFolder = cfolder;
			}

			List<StaticFolderName> subFolders = cfolder.getStaticFolder();
			for (StaticFolderName sub : subFolders) {
				String driveSubFolderPath = FilePath.join(folderRootPath, sub.getFolderName());
				driveStaticFolderPath.add(driveSubFolderPath);

				ResourceFolder resourceFolder = new ResourceFolder(this.config, folderRootPath, driveSubFolderPath, sub,
						cfolder.isWebRootFolder());
				// old code for compliance
				resourceFolderList.add(resourceFolder);
				// //////AAAAA

				// new code for new lookup mechanism
				List<ResourceFolder> list = staticConfigRresourceFolderMap.get(cfolder);
				if (list == null) {
					list = Lists.newArrayList();
					staticConfigRresourceFolderMap.put(cfolder, list);
				}

				list.add(resourceFolder);

				staticRresourceFolderMap.put(sub.getFolderName(), resourceFolder);
				// ///AAAAAAA
			}
		}
	}

	@Override
	public HandleResult lookup(HttpServletRequest request, HttpServletResponse response) {
		// this.printRequestHeader(request);
		String requestPath = UrlHelper.getRequestPath(request);
		// logger.trace("lookup static resource:{0}", requestPath);

		List<StaticResource> resources = this.lookupResource_V2(requestPath, request);

		// for old compliance
		if (resources.size() == 0) {
			resources = this.lookupResource_V1(requestPath, request);
		}

		StaticResource requestResource = null;

		// We need to handle resources lookup if have more than one matched
		// if >1 we need to user the file from webRoot, else null
		if (resources.size() == 1) {
			requestResource = resources.get(0);
		} else if (resources.size() > 1) {
			StringBuilder builder = new StringBuilder();
			for (int i = 0; i < resources.size(); i++) {
				StaticResource r = resources.get(i);
				builder.append("File ").append(i).append(": ");
				builder.append(r.getFilePath()).append("\r\n");
				if (r.isWebRootFile()) {
					requestResource = r;
				}
			}

			if (requestResource == null) {
				requestResource = resources.get(0);
			}

			logger.debug(oneMoreFileMsg, requestPath, builder.toString(), requestResource.getFilePath(),
					requestResource.isWebRootFile());

		}

		HandleResult result = null;

		if (requestResource != null) {
			result = new HandleResult(request, response);
			if (requestResource.isWebRootFile()) {
				response.reset();
				String newPath = requestResource.getWebFilePath();
				if (!newPath.startsWith("/")) {
					newPath = "/" + newPath;
				}
				try {
					// logger.debug("this request would dispatcher to {0}", newPath);
					if (this.debug) {
						request = RequestDebugWrapper.wrap(request);
					}
					request.getRequestDispatcher(newPath).forward(request, response);
				} catch (ServletException | IOException e) {
					logger.error("error on forward request'{0}' to '{1}'", requestPath, newPath);
				} catch (Exception e) {
					logger.error("error on forward request'{0}' to '{1}' with unknow exception", e, requestPath,
							newPath);
				}
			} else {
				this.process(requestResource, request, response);
			}

		}

		// logger.debug("RequestPath[{0}] would dispatcher to {0}", requestPath);
		return result;
	}

	@SuppressWarnings("unused")
	private void printRequestHeader(HttpServletRequest request) {
		if (logger.isDebugEnabled()) {
			logger.debug("{0} = {1}", "RequestURL", request.getRequestURL());
			logger.debug("{0} = {1}", "RequestURI", request.getRequestURI());
			Enumeration<String> headers = request.getHeaderNames();
			while (headers.hasMoreElements()) {
				String h = headers.nextElement();
				logger.debug("Header: {0} = {1}", h, request.getHeader(h));
			}

			// Checking If-Range
			String headerValue = request.getHeader("If-Range");
			logger.debug("header: {0} = {1}", "If-Range", headerValue);
		}
	}

	private void process(StaticResource requestResource, HttpServletRequest request, HttpServletResponse response) {
		String contentType = this.parseMimeType(requestResource, request);
		boolean serveContent = true;

		ServletOutputStream ostream = null;
		PrintWriter writer = null;
		try {
			String eTag = requestResource.getETag();
			String lastModifiedHttp = requestResource.getLastModifiedHttp();
			ArrayList<Range> ranges = null;
			long contentLength = -1L;

			if (useAcceptRanges) {
				// Accept ranges header
				response.setHeader("Accept-Ranges", "bytes");
			}
			// Parse range specifier
			ranges = parseRange(request, response, requestResource);

			// ETag header
			response.setHeader("ETag", eTag);

			// Last-Modified header
			response.setHeader("Last-Modified", lastModifiedHttp);

			// Get content length
			contentLength = requestResource.getContentLength();
			// Special case for zero length files, which would cause a
			// (silent) ISE when setting the output buffer size
			if (contentLength == 0L) {
				serveContent = false;
			}

			if (serveContent) {
				// Trying to retrieve the servlet output stream
				try {
					ostream = response.getOutputStream();
				} catch (IllegalStateException e) {
					// If it fails, we try to get a Writer instead if we're
					// trying to serve a text file
					if (((contentType == null) || (contentType.startsWith("text")) || (contentType.endsWith("xml"))
							|| (contentType.contains("/javascript")))) {
						writer = response.getWriter();
						// Cannot reliably serve partial content with a Writer
						ranges = FULL;
					} else {
						throw e;
					}
				}
			}

			if (((ranges == null || ranges.isEmpty()) && request.getHeader("Range") == null) || ranges == FULL) {
				// Set the appropriate output headers
				if (serveContent) {
					ostream = response.getOutputStream();
				}
				if (contentType != null) {
					response.setContentType(contentType);
				}
				if (contentLength >= 0 && (!serveContent || ostream != null)) {
					response.setContentLength((int) contentLength);
				}

				if (serveContent) {
					try {
						response.setBufferSize(output);
					} catch (IllegalStateException e) {
						// Silent catch
					}
					InputStream renderResult = null;
					if (ostream == null) {
						// Output via a writer so can't use sendfile or write
						// content directly.

						renderResult = requestResource.getInputStream();
						copy(requestResource, renderResult, writer, fileEncoding);
					} else {
						// Output is content of resource
						if (!checkSendfile(request, response, requestResource, contentLength, null)) {
							// sendfile not possible so check if resource
							// content is available directly
							byte[] resourceBody = requestResource.getContent();
							if (resourceBody == null) {
								// Resource content not available, use
								// inputstream
								renderResult = requestResource.getInputStream();
							} else {
								// Use the resource content directly
								ostream.write(resourceBody);
							}
						}
						// If a stream was configured, it needs to be copied to
						// the output (this method closes the stream)
						if (renderResult != null) {
							copy(requestResource, renderResult, ostream);
						}
					}
				}
			} else {

				if ((ranges == null) || (ranges.isEmpty()))
					return;

				// Partial content response.
				response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

				if (ranges.size() == 1) {
					Range range = ranges.get(0);
					response.addHeader("Content-Range", "bytes " + range.start + "-" + range.end + "/" + range.length);
					long length = range.end - range.start + 1;
					response.setContentLength((int) length);
					if (contentType != null) {
						response.setContentType(contentType);
					}
					try {
						response.setBufferSize(output);
					} catch (IllegalStateException e) {
						// Silent catch
					}
					if (ostream != null) {
						if (!checkSendfile(request, response, requestResource, range.end - range.start + 1, range))
							copy(requestResource, ostream, range);
					} else {
						// we should not get here
						throw new IllegalStateException();
					}
				} else {
					response.setContentType("multipart/byteranges; boundary=" + mimeSeparation);
					if (serveContent) {
						try {
							response.setBufferSize(output);
						} catch (IllegalStateException e) {
							// Silent catch
						}
						if (ostream != null) {
							copy(requestResource, ostream, ranges.iterator(), contentType);
						} else {
							// we should not get here
							throw new IllegalStateException();
						}
					}
				}
			}

			// response.setContentLength((int) requestResource.getContentLength());
			// response.setBufferSize(output);
			// byte[] resourceBody = requestResource.getContent();
			// // Use the resource content directly
			// ostream.write(resourceBody);
			// ostream.close();
		} catch (IOException e) {
			logger.debug("render static file fail for [{0}]", e, requestResource.getFilePath());
			try {
				if (!response.isCommitted()) {
					response.reset();
					response.resetBuffer();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} catch (IOException e1) {

			}
		} catch (Exception e) {
			logger.debug("render static file fail for [{0}]", e, requestResource.getFilePath());
			try {
				if (!response.isCommitted()) {
					response.reset();
					response.resetBuffer();
					response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			} catch (IOException e1) {

			}
		}

	}

	private void copy(StaticResource requestResource, InputStream is, ServletOutputStream ostream) throws IOException {

		IOException exception = null;
		InputStream istream = new BufferedInputStream(is, input);

		// Copy the input stream to the output stream
		exception = copyRange(istream, ostream);

		// Clean up the input stream
		istream.close();

		// Rethrow any exception that has occurred
		if (exception != null)
			throw exception;

	}

	private IOException copyRange(InputStream istream, ServletOutputStream ostream) {
		// Copy the input stream to the output stream
		IOException exception = null;
		byte buffer[] = new byte[input];
		int len = buffer.length;
		while (true) {
			try {
				len = istream.read(buffer);
				if (len == -1)
					break;
				ostream.write(buffer, 0, len);
			} catch (IOException e) {
				exception = e;
				len = -1;
				break;
			}
		}
		return exception;
	}

	/**
	 * Copy the contents of the specified input stream to the specified output stream, and ensure that both streams are
	 * closed before returning (even in the face of an exception).
	 *
	 * @param resource
	 *            The source resource
	 * @param is
	 *            The input stream to read the source resource from
	 * @param writer
	 *            The writer to write to
	 * @param encoding
	 *            The encoding to use when reading the source input stream
	 *
	 * @exception IOException
	 *                if an input/output error occurs
	 */
	protected void copy(StaticResource resource, InputStream is, PrintWriter writer, String encoding)
			throws IOException {

		IOException exception = null;

		InputStream resourceInputStream = resource.getInputStream();

		Reader reader;
		if (encoding == null) {
			reader = new InputStreamReader(resourceInputStream);
		} else {
			reader = new InputStreamReader(resourceInputStream, encoding);
		}

		// Copy the input stream to the output stream
		exception = copyRange(reader, writer);

		// Clean up the reader
		reader.close();

		// Rethrow any exception that has occurred
		if (exception != null)
			throw exception;
	}

	private IOException copyRange(Reader reader, PrintWriter writer) {
		// Copy the input stream to the output stream
		IOException exception = null;
		char buffer[] = new char[input];
		int len = buffer.length;
		while (true) {
			try {
				len = reader.read(buffer);
				if (len == -1)
					break;
				writer.write(buffer, 0, len);
			} catch (IOException e) {
				exception = e;
				len = -1;
				break;
			}
		}
		return exception;
	}

	private void copy(StaticResource resource, ServletOutputStream ostream, Iterator<Range> ranges, String contentType)
			throws IOException {

		IOException exception = null;

		while ((exception == null) && (ranges.hasNext())) {

			InputStream resourceInputStream = resource.getInputStream();
			try (InputStream istream = new BufferedInputStream(resourceInputStream, input)) {

				Range currentRange = ranges.next();

				// Writing MIME header.
				ostream.println();
				ostream.println("--" + mimeSeparation);
				if (contentType != null)
					ostream.println("Content-Type: " + contentType);
				ostream.println("Content-Range: bytes " + currentRange.start + "-" + currentRange.end + "/"
						+ currentRange.length);
				ostream.println();

				// Printing content
				exception = copyRange(istream, ostream, currentRange.start, currentRange.end);
			}
		}

		ostream.println();
		ostream.print("--" + mimeSeparation + "--");

		// Rethrow any exception that has occurred
		if (exception != null)
			throw exception;

	}

	private void copy(StaticResource resource, ServletOutputStream ostream, Range range) throws IOException {
		IOException exception = null;
		InputStream resourceInputStream = resource.getInputStream();
		InputStream istream = new BufferedInputStream(resourceInputStream, input);
		exception = copyRange(istream, ostream, range.start, range.end);

		// Clean up the input stream
		istream.close();

		// Rethrow any exception that has occurred
		if (exception != null)
			throw exception;
	}

	private IOException copyRange(InputStream istream, ServletOutputStream ostream, long start, long end) {
		logger.debug("Serving bytes:" + start + "-" + end);

		long skipped = 0;
		try {
			skipped = istream.skip(start);
		} catch (IOException e) {
			return e;
		}
		if (skipped < start) {
			String error = Strings.fillArgs("defaultservlet.skipfail skipped = {0}, start = {1}", Long.valueOf(skipped),
					Long.valueOf(start));
			return new IOException(error);
		}

		IOException exception = null;
		long bytesToRead = end - start + 1;

		byte buffer[] = new byte[input];
		int len = buffer.length;
		while ((bytesToRead > 0) && (len >= buffer.length)) {
			try {
				len = istream.read(buffer);
				if (bytesToRead >= len) {
					ostream.write(buffer, 0, len);
					bytesToRead -= len;
				} else {
					ostream.write(buffer, 0, (int) bytesToRead);
					bytesToRead = 0;
				}
			} catch (IOException e) {
				exception = e;
				len = -1;
			}
			if (len < buffer.length)
				break;
		}

		return exception;
	}

	protected boolean checkSendfile(HttpServletRequest request, HttpServletResponse response, StaticResource resource,
			long contentLength, Range range) {
		if (range == null) {
			return false;
		}

		// not use currently, always false

		// if (sendfileSize > 0 && length > sendfileSize && (resource.getCanonicalPath() != null)
		// && (Boolean.TRUE == request.getAttribute(Globals.SENDFILE_SUPPORTED_ATTR))
		// && (request.getClass().getName().equals("org.apache.catalina.connector.RequestFacade"))
		// && (response.getClass().getName().equals("org.apache.catalina.connector.ResponseFacade"))) {
		// request.setAttribute(Globals.SENDFILE_FILENAME_ATTR, resource.getCanonicalPath());
		// if (range == null) {
		// request.setAttribute(Globals.SENDFILE_FILE_START_ATTR, Long.valueOf(0L));
		// request.setAttribute(Globals.SENDFILE_FILE_END_ATTR, Long.valueOf(length));
		// } else {
		// request.setAttribute(Globals.SENDFILE_FILE_START_ATTR, Long.valueOf(range.start));
		// request.setAttribute(Globals.SENDFILE_FILE_END_ATTR, Long.valueOf(range.end + 1));
		// }
		// return true;
		// }
		// return false;
		return false;
	}

	private ArrayList<Range> parseRange(HttpServletRequest request, HttpServletResponse response,
			StaticResource resource) throws IOException {
		// Checking If-Range
		String headerValue = request.getHeader("If-Range");

		if (headerValue != null) {

			long headerValueTime = (-1L);
			try {
				headerValueTime = request.getDateHeader("If-Range");
			} catch (IllegalArgumentException e) {
				// Ignore
			}

			String eTag = resource.getETag();
			long lastModified = resource.getLastModified();

			if (headerValueTime == (-1L)) {

				// If the ETag the client gave does not match the entity
				// etag, then the entire entity is returned.
				if (!eTag.equals(headerValue.trim()))
					return FULL;

			} else {

				// If the timestamp of the entity the client got is older than
				// the last modification date of the entity, the entire entity
				// is returned.
				if (lastModified > (headerValueTime + 1000))
					return FULL;

			}

		}

		long fileLength = resource.getContentLength();

		if (fileLength == 0)
			return null;

		// Retrieving the range header (if any is specified
		String rangeHeader = request.getHeader("Range");

		if (rangeHeader == null)
			return null;
		// bytes is the only range unit supported (and I don't see the point
		// of adding new ones).
		if (!rangeHeader.startsWith("bytes")) {
			response.addHeader("Content-Range", "bytes */" + fileLength);
			response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
			return null;
		}

		rangeHeader = rangeHeader.substring(6);

		// Vector which will contain all the ranges which are successfully
		// parsed.
		ArrayList<Range> result = new ArrayList<>();
		StringTokenizer commaTokenizer = new StringTokenizer(rangeHeader, ",");

		// Parsing the range list
		while (commaTokenizer.hasMoreTokens()) {
			String rangeDefinition = commaTokenizer.nextToken().trim();

			Range currentRange = new Range();
			currentRange.length = fileLength;

			int dashPos = rangeDefinition.indexOf('-');

			if (dashPos == -1) {
				response.addHeader("Content-Range", "bytes */" + fileLength);
				response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
				return null;
			}

			if (dashPos == 0) {

				try {
					long offset = Long.parseLong(rangeDefinition);
					currentRange.start = fileLength + offset;
					currentRange.end = fileLength - 1;
				} catch (NumberFormatException e) {
					response.addHeader("Content-Range", "bytes */" + fileLength);
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					return null;
				}

			} else {

				try {
					currentRange.start = Long.parseLong(rangeDefinition.substring(0, dashPos));
					if (dashPos < rangeDefinition.length() - 1)
						currentRange.end = Long
								.parseLong(rangeDefinition.substring(dashPos + 1, rangeDefinition.length()));
					else
						currentRange.end = fileLength - 1;
				} catch (NumberFormatException e) {
					response.addHeader("Content-Range", "bytes */" + fileLength);
					response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
					return null;
				}

			}

			if (!currentRange.validate()) {
				response.addHeader("Content-Range", "bytes */" + fileLength);
				response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
				return null;
			}

			result.add(currentRange);
		}

		return result;
	}

	private String parseMimeType(StaticResource requestResource, HttpServletRequest request) {
		return request.getSession().getServletContext().getMimeType(requestResource.getFilePath());
	}

	private List<StaticResource> lookupResource_V1(String requestPath, HttpServletRequest request) {
		List<StaticResource> list = Lists.newArrayList();

		for (ResourceFolder folder : resourceFolderList) {
			StaticResource resource = folder.lookup(requestPath);
			if (resource != null) {
				list.add(resource);
			}
		}

		return list;
	}

	private List<StaticResource> lookupResource_V2(String requestPath, HttpServletRequest request) {
		List<StaticResource> list = Lists.newArrayList();

		// first lookup from web root
		if (this.webRootFolder != null) {
			List<ResourceFolder> rList = this.staticConfigRresourceFolderMap.get(this.webRootFolder);
			for (ResourceFolder folder : rList) {
				StaticResource resource = folder.lookup(requestPath);
				if (resource != null) {
					list.add(resource);
				}
			}
		}

		String firstFolderPath = this.parseFirstFolderPath(requestPath);

		if (firstFolderPath != null) {
			ResourceFolder folder = this.staticRresourceFolderMap.get(firstFolderPath);
			if (folder != null) {
				StaticResource resource = folder.lookup_v2(requestPath);
				if (resource != null) {
					list.add(resource);
				}
			}
		}

		return list;
	}

	private String parseFirstFolderPath(String requestPath) {
		String temp = requestPath;
		if (temp.startsWith("/")) {
			temp = temp.substring(1);
		}

		String folder = Strings.subStringBefore(temp, '/');

		if (folder.length() == 0) {
			return null;
		}

		return folder;
	}

	protected static class Range {

		public long start;

		public long end;

		public long length;

		/**
		 * Validate range.
		 *
		 * @return true if the range is valid, otherwise false
		 */
		public boolean validate() {
			if (end >= length)
				end = length - 1;
			return (start >= 0) && (end >= 0) && (start <= end) && (length > 0);
		}
	}

}
