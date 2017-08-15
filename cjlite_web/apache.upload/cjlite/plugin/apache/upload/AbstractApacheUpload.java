package cjlite.plugin.apache.upload;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.plugin.upload.FileUpload;
import cjlite.plugin.upload.UploadConfig;
import cjlite.web.interceptor.ControllerInterceptor;
import cjlite.web.interceptor.ControllerInvocation;

/**
 * Pls DO NOT use this plugin, it is not finished
 * 
 * @author YunYang
 * @version Sep 18, 2015 11:40:57 AM 
 */
public abstract class AbstractApacheUpload implements FileUpload, ControllerInterceptor {

	private static final Logger logger = Logger.thisClass();

	@Inject
	Config config;

	private boolean initialized = false;
	private UploadConfig uploadConfig;

	private void internalInitialize() {
		if (!this.initialized) {
			uploadConfig = this.buildUploadConfig(this.config);
			if (uploadConfig == null) {
				throw new RuntimeException(
						"Error on perform buildUploadConfig method, the returned UploadConfig can not be null");
			}
		}
		this.initialized = true;
	}

	@Override
	public Object intercept(ControllerInvocation invocation) throws Exception {
		this.internalInitialize();
		this.handleFileUpload(invocation);
		return invocation.invoke();
	}

	private void handleFileUpload(ControllerInvocation invocation) {
		if (!ServletFileUpload.isMultipartContent(invocation.getRequest())) {
			return;
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();

		// Configure a repository (to ensure a secure temp location is used)
		ServletContext servletContext = invocation.getRequest().getSession().getServletContext();
		File repository = (File) servletContext.getAttribute("javax.servlet.context.tempdir");
		factory.setRepository(repository);
		logger.debug("file dir:{0}", repository.getAbsolutePath());

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		try {
			List<FileItem> items = upload.parseRequest(invocation.getRequest());
			logger.debug("items length:{0}", items.size());
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
	}

}
