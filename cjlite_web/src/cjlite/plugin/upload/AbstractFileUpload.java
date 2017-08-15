/**
 * 
 */
package cjlite.plugin.upload;

import java.io.IOException;

import javax.inject.Inject;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.Strings;
import cjlite.web.interceptor.ControllerInterceptor;
import cjlite.web.interceptor.ControllerInvocation;
import cjlite.web.interceptor.InterceptorChain;

/**
 * @author YunYang
 * @version Jul 30, 2015 9:48:32 AM
 */
public abstract class AbstractFileUpload implements FileUpload, ControllerInterceptor {
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

	protected void handleFileUpload(ControllerInvocation invocation) {
		String type = invocation.getRequest().getHeader("Content-Type");
		// If this is not a multipart/form-data request continue
		if (type == null || !type.startsWith("multipart/form-data")) {
			return;
		}
		
		DefaultUploadResult result = new DefaultUploadResult();
		try {

			MultipartUploadRequest mpreq = new MultipartUploadRequest(invocation.getRequest(), this.config,
					this.uploadConfig, result);
		} catch (IOException e) {
			String error = Strings.fillArgs("error on process a multipart request, because: {0}", e.getMessage());
			logger.error(error, e);
			result.addError(error);
		}

		if (InterceptorChain.class.isInstance(invocation)) {
			InterceptorChain chain = (InterceptorChain) invocation;
			chain.reWorkMethodParam(UploadResult.class, result);
		}
	}
}
