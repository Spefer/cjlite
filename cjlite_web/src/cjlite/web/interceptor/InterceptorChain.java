/**
 * 
 */
package cjlite.web.interceptor;

import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.log.Logger;
import cjlite.utils.Strings;
import cjlite.web.handler.PathVariables;
import cjlite.web.helper.ControllerHelper;
import cjlite.web.mvc.MethodParameter;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.PathMapping;
import cjlite.web.mvc.RequestContext;

/**
 * @author YunYang
 * 
 */
public class InterceptorChain implements ControllerInvocation {

	private static final Logger logger = Logger.thisClass();

	private final List<ControllerInterceptor> interceptorList;
	private final Object invokeObject;
	private final PathMapping mapping;
	private final Object[] paramValues;
	private final HttpServletRequest request;
	private final HttpServletResponse response;
	private int index = -1;

	private final PathVariables pathVariables;

	public InterceptorChain(HttpServletRequest request, HttpServletResponse response,
			List<ControllerInterceptor> cilist, Object controllerInstance, PathMapping mapping, Object[] paramValues,
			PathVariables pathVariables) {
		this.request = request;
		this.response = response;
		this.interceptorList = cilist;
		this.invokeObject = controllerInstance;
		this.mapping = mapping;
		this.paramValues = paramValues;
		this.pathVariables = pathVariables;
	}

	public ModelView invoke() throws InterceptorException {
		index += 1;
		if (index < interceptorList.size()) {
			ControllerInterceptor interceptor = interceptorList.get(index);
			ModelView mv = null;
			try {
				mv = (ModelView) interceptor.intercept(this);
				return mv;
			} catch (Exception e) {
				String msg = Strings.fillArgs("error in perform intercept on {0}, because : {1}", interceptor
						.getClass().getName(), e.getMessage());
				throw new InterceptorException(msg, e);
			}
		} else {
			int len = paramValues.length;
			if (len == 0) {
				return ControllerHelper.invoke(invokeObject, mapping);
			} else {
				return ControllerHelper.invoke(invokeObject, mapping, paramValues);
			}
		}
	}

	@Override
	public HttpServletRequest getRequest() {
		return this.request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return this.response;
	}

	@Override
	public Method getInvokeMethod() {
		return this.mapping.getMappingMethod();
	}

	@Override
	public PathMapping getPathMapping() {
		return this.mapping;
	}

	@Override
	public Object getInvokeObject() {
		return this.invokeObject;
	}

	public void reWorkMethodParam(Class<?> resultClass, Object result) {
		MethodParameter[] params = mapping.getMethodParameters();
		for (int i = 0; i < params.length; i++) {
			MethodParameter param = params[i];
			if (RequestContext.class.equals(param.getParamType())) {
				RequestContext context = (RequestContext) this.paramValues[i];
				context.add(resultClass, result);
			}
		}
	}

	@Override
	public PathVariables getPathVariables() {
		return this.pathVariables;
	}

	@Override
	public Object getMethodParam(int i) {
		return this.paramValues[i];
	}

}
