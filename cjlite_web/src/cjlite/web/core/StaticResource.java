/**
 * 
 */
package cjlite.web.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import cjlite.log.Logger;
import cjlite.utils.Strings;

/**
 * @author ming
 *
 */
final class StaticResource {

	private static final Logger logger = Logger.thisClass();

	private final String filePath;
	private final String webFilePath;
	private final boolean webRootFolder;
	private final ResourceFolder resourceFolder;
	private final File resource;
	private volatile String weakETag;

	private InputStream cacheFileInputStream;

	public StaticResource(ResourceFolder resourceFolder, String filePath, String webFilePath, boolean webRootFolder,
			File resource) {
		this.resourceFolder = resourceFolder;
		this.filePath = filePath;
		this.webFilePath = webFilePath;
		this.webRootFolder = webRootFolder;
		this.resource = resource;
	}

	public String getFilePath() {
		return this.filePath;
	}

	public boolean isWebRootFile() {
		return webRootFolder;
	}

	public String getWebFilePath() {
		return webFilePath;
	}

	public long getLastModified() {
		return resource.lastModified();
	}

	public long getContentLength() {
		return resource.length();
	}

	public final String getETag() {
		if (weakETag == null) {
			synchronized (this) {
				if (weakETag == null) {
					long contentLength = getContentLength();
					long lastModified = getLastModified();
					if ((contentLength >= 0) || (lastModified >= 0)) {
						weakETag = "W/\"" + contentLength + "-" + lastModified + "\"";
					}
				}
			}
		}
		return weakETag;
	}

	public final byte[] getContent() {
		long len = getContentLength();

		if (len > Integer.MAX_VALUE) {
			// Can't create an array that big
			String error = Strings.fillArgs("Can't create an array that big for file[{0}][length:{1}]",
					this.webFilePath, Long.valueOf(len));
			throw new ArrayIndexOutOfBoundsException(error);
		}

		int size = (int) len;
		byte[] result = new byte[size];

		int pos = 0;
		try (InputStream is = new FileInputStream(resource)) {
			while (pos < size) {
				int n = is.read(result, pos, size - pos);
				if (n < 0) {
					break;
				}
				pos += n;
			}
		} catch (IOException ioe) {
			logger.debug("get content file[{0}] fail", ioe, this.webFilePath);
		}

		return result;
	}

	public String getLastModifiedHttp() {
		return ConcurrentDateFormat.formatRfc1123(new Date(getLastModified()));
	}

	public InputStream getInputStream() {
		if (this.cacheFileInputStream == null) {
			this.cacheFileInputStream = doGetInputStream();
		}
		return this.cacheFileInputStream;
	}

	protected InputStream doGetInputStream() {
		try {
			return new FileInputStream(this.resource);
		} catch (FileNotFoundException fnfe) {
			// Race condition (file has been deleted) - not an error
			return null;
		}
	}

}
