package cjlite.plugin.upload;

import javax.servlet.http.HttpServletRequest;

import com.oreilly.servlet.multipart.FileRenamePolicy;

public abstract class UploadConfig {

	public abstract String getStaticFolder();

	public abstract int getMaxPostSize();

	public abstract String getSaveDirectory(HttpServletRequest request);

	public abstract String getEncoding();

	public abstract FileRenamePolicy getFileRenamePolicy(HttpServletRequest request);

	/**
	 * You can override this method to config the static folder path from WebRoot or path in appconfig.properties
	 * 
	 * @return
	 */
	public String getConfigFolderKey() {
		return "RootPath";
	}
}
