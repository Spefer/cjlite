package cjlite.plugin.upload;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUtils;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.FilePath;
import cjlite.utils.Lists;
import cjlite.utils.Strings;
import cjlite.web.ConfigFolder;

import com.oreilly.servlet.UploadedFile;
import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;

public class MultipartUploadRequest {

	private static final Logger logger = Logger.thisClass();

	private static final String default_uploadDir = "/temp";

	protected Hashtable<Object, Vector<String>> parameters = new Hashtable<Object, Vector<String>>(); // name - Vector
																										// of values
	protected Hashtable<String, UploadedFile> files = new Hashtable<String, UploadedFile>(); // name - UploadedFile

	private final UploadConfig uploadConfig;
	private final Config config;

	public MultipartUploadRequest(HttpServletRequest request, Config config, UploadConfig uploadConfig,
			DefaultUploadResult result) throws IOException {
		this.config = config;
		this.uploadConfig = uploadConfig;
		this.processUploadRequest(request, result);
	}

	private void processUploadRequest(HttpServletRequest request, DefaultUploadResult result) throws IOException {

		File saveDir = this.preValidate(request, result);

		// Parse the incoming multipart, storing files in the dir provided,
		// and populate the meta objects which describe what we found
		MultipartParser parser = new MultipartParser(request, this.uploadConfig.getMaxPostSize(), true, true,
				this.uploadConfig.getEncoding());

		// Some people like to fetch query string parameters from
		// MultipartRequest, so here we make that possible. Thanks to
		// Ben Johnson, ben.johnson@merrillcorp.com, for the idea.
		if (request.getQueryString() != null) {
			// Let HttpUtils create a name->String[] structure
			Hashtable<String, String[]> queryParameters = HttpUtils.parseQueryString(request.getQueryString());
			// For our own use, name it a name->Vector structure
			Enumeration<String> queryParameterNames = queryParameters.keys();
			while (queryParameterNames.hasMoreElements()) {
				String paramName = queryParameterNames.nextElement();
				String[] values = queryParameters.get(paramName);
				Vector<String> newValues = new Vector<String>();
				for (int i = 0; i < values.length; i++) {
					newValues.add(values[i]);
				}
				parameters.put(paramName, newValues);
			}
		}

		Part part;
		while ((part = parser.readNextPart()) != null) {
			String name = part.getName();
			if (name == null) {
				throw new IOException("Malformed input: parameter name missing (known Opera 7 bug)");
			}
			if (part.isParam()) {
				// It's a parameter part, add it to the vector of values
				ParamPart paramPart = (ParamPart) part;
				String value = paramPart.getStringValue();
				Vector<String> existingValues = parameters.get(name);
				if (existingValues == null) {
					existingValues = new Vector<String>();
					parameters.put(name, existingValues);
				}
				existingValues.addElement(value);
			} else if (part.isFile()) {
				// It's a file part
				FilePart filePart = (FilePart) part;
				String fileName = filePart.getFileName();
				if (fileName != null) {
					filePart.setRenamePolicy(this.uploadConfig.getFileRenamePolicy(request)); // null policy is OK
					// The part actually contained a file
					filePart.writeTo(saveDir);
					files.put(name,
							new UploadedFile(saveDir.toString(), filePart.getFileName(), fileName, filePart
									.getContentType(), uploadConfig.getSaveDirectory(request)));
				}
				// else {
				// // The field did not contain a file
				// files.put(name, new UploadedFile(null, null, null, null, null,null));
				// }
			}
		}

		result.setParams(this.parameters);
		result.setFiles(this.files);
	}

	private File preValidate(HttpServletRequest request, DefaultUploadResult result) {
		// Sanity check values
		String configedSaveDirectory = this.uploadConfig.getSaveDirectory(request);
		if (request == null)
			throw new IllegalArgumentException("request cannot be null");
		if (configedSaveDirectory == null)
			throw new IllegalArgumentException("saveDirectory cannot be null");
		if (this.uploadConfig.getMaxPostSize() <= 0) {
			throw new IllegalArgumentException("maxPostSize must be positive");
		}

		ConfigFolder cfolder = this.config.getConfigFolder(this.uploadConfig.getConfigFolderKey());

		String root = this.config.getProperties(this.uploadConfig.getConfigFolderKey());

		if (root == null) {
			String msg = Strings.fillArgs(
					"The value for configFolderKey[{0}] is not exist OR configFolderKey is not correct!",
					this.uploadConfig.getConfigFolderKey());
			throw new IllegalArgumentException(msg);
		}

		if (!cfolder.contain(uploadConfig.getStaticFolder())) {
			String msg = Strings.fillArgs(
					"The static folder[{0}] is not exist in ConfigFolder[{1}](which point to {2})",
					uploadConfig.getStaticFolder(), this.uploadConfig.getConfigFolderKey(), root);
			throw new IllegalArgumentException(msg);
		}

		String upload_dir = configedSaveDirectory;
		if (upload_dir == null) {
			upload_dir = this.config.getProperties("upload_directory", default_uploadDir);
		}
		String dynamicPath = null;
		if (result.getParameters().get("parent_path") != null) {
			dynamicPath = result.getParameters().get("parent_path").get(0);
		}
		String saveDirectory = null;
		if (dynamicPath == null) {
			saveDirectory = FilePath.join(root, this.uploadConfig.getStaticFolder(), upload_dir);
		} else {
			saveDirectory = FilePath.join(root, this.uploadConfig.getStaticFolder(), upload_dir, dynamicPath);
		}

		// Save the dir
		File dir = new File(saveDirectory);

		// Check saveDirectory is truly a directory
		if (!dir.isDirectory()) {
			dir.mkdirs();
		}

		// Check saveDirectory is writable
		if (!dir.canWrite()) {
			String error = Strings.fillArgs("Not writable: {0}", this.uploadConfig.getSaveDirectory(request));
			logger.debug(error);
			throw new IllegalArgumentException(error);
		}

		return dir;
	}

}

class DefaultUploadResult implements UploadResult {

	private List<String> errors = Lists.newArrayList();
	protected Hashtable<Object, Vector<String>> parameters = new Hashtable<Object, Vector<String>>(); // name - Vector
	// of values
	protected Hashtable<String, UploadedFile> files = new Hashtable<String, UploadedFile>(); // name - UploadedFile

	public void setFiles(Hashtable<String, UploadedFile> filesTable) {
		if (filesTable == null) {
			return;
		}
		files.putAll(filesTable);
		for (String key : filesTable.keySet()) {
			UploadedFile file = filesTable.get(key);
			Vector<String> vec = parameters.get(key);
			if (vec == null) {
				vec = new Vector<String>();
				parameters.put(key, vec);
			}
			vec.add(file.getOriginalFileName());
		}
	}

	public void setParams(Hashtable<Object, Vector<String>> parameters2) {
		if (parameters2 == null) {
			return;
		}
		parameters.putAll(parameters2);
	}

	@Override
	public Hashtable<Object, Vector<String>> getParameters() {
		return parameters;
	}

	@Override
	public Hashtable<String, UploadedFile> getUploadedFiles() {
		return files;
	}

	@Override
	public boolean isSuccessful() {
		return errors.size() == 0;
	}

	public void addError(String error) {
		errors.add(error);
	}

	@Override
	public List<String> getErrors() {
		return this.errors;
	}

	@Override
	public String getParam(String name) {
		Vector<String> vec = parameters.get(name);
		if (vec == null) {
			return null;
		}
		return vec.size() == 0 ? null : vec.get(0);
	}

	public Vector<String> getParams(String name) {
		Vector<String> vec = parameters.get(name);
		return vec;
	}

	@Override
	public UploadedFile getFile(String fileName) {
		return this.files.get(fileName);
	}
}
