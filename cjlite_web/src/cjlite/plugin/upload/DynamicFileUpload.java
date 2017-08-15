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
 * @version Dec 13, 2015 6:09:09 PM
 */
public abstract class DynamicFileUpload implements ControllerInterceptor {
	private static final Logger logger = Logger.thisClass();

	@Inject
	private Config config;

	@Override
	public Object intercept(ControllerInvocation invocation) throws Exception {
		this.handleFileUpload(invocation);
		return invocation.invoke();
	}

	private void handleFileUpload(ControllerInvocation invocation) {
		String type = invocation.getRequest().getHeader("Content-Type");
		// If this is not a multipart/form-data request continue
		if (type == null || !type.startsWith("multipart/form-data")) {
			return;
		}
		DefaultUploadResult result = new DefaultUploadResult();
		try {
			UploadConfig uploadConfig = this.buildUploadConfig(invocation, this.config);
			@SuppressWarnings("unused")
			MultipartUploadRequest mpreq = new MultipartUploadRequest(invocation.getRequest(), this.config,
					uploadConfig, result);
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

	protected abstract UploadConfig buildUploadConfig(ControllerInvocation invocation, Config config);

}
