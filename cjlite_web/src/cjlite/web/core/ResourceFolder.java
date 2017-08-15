/**
 * 
 */
package cjlite.web.core;

import java.io.File;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.FilePath;
import cjlite.web.StaticFolderName;

/**
 * @author ming
 *
 */
final class ResourceFolder {

	private static final Logger logger = Logger.thisClass();
	private final Config config;
	private final String folderRootPath;
	private final String subFolderPath;
	private final boolean webRootFolder;
	private final StaticFolderName subFolderName;

	public ResourceFolder(Config config, String folderRootPath, String subFolderPath, StaticFolderName sub,
			boolean webRootFolder) {
		this.config = config;
		this.folderRootPath = folderRootPath;
		this.subFolderPath = subFolderPath;
		this.webRootFolder = webRootFolder;
		this.subFolderName = sub;
	}

	public StaticResource lookup(String requestPath) {
		String filePath = FilePath.join(this.subFolderPath, requestPath);
		File file = new File(filePath);

		boolean result = file.exists() && !file.isDirectory();
		// logger.debug("File:{0}; Exist:{1}", filePath, result);

		if (result) {
			String webFilePath = FilePath.join(subFolderName.getFolderName(), requestPath);
			return new StaticResource(this, filePath, webFilePath, webRootFolder, file);
		}

		return null;
	}

	public StaticResource lookup_v2(String requestPath) {
		String filePath = FilePath.join(this.folderRootPath, requestPath);
		File file = new File(filePath);

		boolean result = file.exists() && !file.isDirectory();
		// logger.debug("File:{0}; Exist:{1}", filePath, result);

		if (result) {
			return new StaticResource(this, filePath, requestPath, webRootFolder, file);
		}

		return null;
	}

}
