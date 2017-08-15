/**
 * 
 */
package cjlite.plugin.upload;

import java.io.File;
import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartWrapper;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.FilePath;
import cjlite.web.handler.Handler;
import cjlite.web.handler.HandlerChain;

/**
 * @author kevin
 * 
 */
@Deprecated
public class MultiPartHandler implements Handler {
	private static final Logger logger = Logger.thisClass();
	private static final String default_uploadDir = "web_upload";

	@Inject
	private Config config;
	private String uploadDir;

	@Override
	public String getName() {
		return "MultiPartHandler";
	}

	@Override
	public void initial() {
		String root = this.config.getProperties("RootPath");
		String upload_dir = this.config.getProperties("upload_directory", default_uploadDir);
		uploadDir = FilePath.join(root, upload_dir);
		//
		File file=new File(uploadDir);
		if(!file.isDirectory()){
			file.mkdirs();
		}
	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain handlerChain) {
		String type = request.getHeader("Content-Type");
		// If this is not a multipart/form-data request continue
		if (type == null || !type.startsWith("multipart/form-data")) {
			handlerChain.handle(request, response);
		} else {
			MultipartWrapper multi;
			try {
				multi = new MultipartWrapper(request, uploadDir);
				handlerChain.handle(multi, response);
			} catch (IOException e) {
				logger.error("error on create a multipart request wrapper", e);
			}

		}
	}
}
