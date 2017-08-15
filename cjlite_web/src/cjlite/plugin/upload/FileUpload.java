/**
 * 
 */
package cjlite.plugin.upload;

import cjlite.config.Config;

/**
 * @author YunYang
 * @version Jul 30, 2015 5:17:50 PM
 */
public interface FileUpload {
	/**
	 * build file upload config
	 * 
	 * @param config
	 * @return
	 */
	UploadConfig buildUploadConfig(Config config);
	
	
}
