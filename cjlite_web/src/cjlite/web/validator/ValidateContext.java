/**
 * 
 */
package cjlite.web.validator;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.plugin.upload.UploadResult;
import cjlite.web.interceptor.ControllerInvocation;
import cjlite.web.interceptor.InterceptorChain;
import cjlite.web.mvc.MethodParameter;
import cjlite.web.mvc.RequestContext;

/**
 * @author YunYang
 * @version Jul 2, 2015 10:36:00 AM
 */
public class ValidateContext {

	private final ControllerInvocation invocation;
	private RequestContext context;
	private UploadResult uploadResult;
	private boolean uploadForm;

	public ValidateContext(ControllerInvocation invocation) {
		this.invocation = invocation;
		this.initial();
	}

	private void initial() {
		if (InterceptorChain.class.isInstance(this.invocation)) {
			InterceptorChain chain = (InterceptorChain) invocation;
			MethodParameter[] params = chain.getPathMapping().getMethodParameters();
			for (int i = 0; i < params.length; i++) {
				MethodParameter param = params[i];
				if (RequestContext.class.equals(param.getParamType())) {
					context = (RequestContext) chain.getMethodParam(i);
					if (context != null) {
						uploadResult = context.getResult(UploadResult.class);
					}
				}
			}
		}
		
		String type = invocation.getRequest().getHeader("Content-Type");
		// If this is not a multipart/form-data request continue
		this.uploadForm = type != null && type.startsWith("multipart/form-data");
	}

	/**
	 * @return HttpServletRequest
	 */
	HttpServletRequest getRequest() {
		return this.invocation.getRequest();
	}

	/**
	 * @return HttpServletResponse
	 */
	HttpServletResponse getResponse() {
		return this.invocation.getResponse();
	}

	public String getParameter(String paramName) {
		String param = this.getRequest().getParameter(paramName);
		if (param != null) {
			return param;
		}

		if (uploadResult != null) {
			param = uploadResult.getParam(paramName);
		}
		
		return param;
	}

}
