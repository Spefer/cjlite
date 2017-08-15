/**
 * 
 */
package cjlite.utils;

import org.aopalliance.intercept.MethodInvocation;

import cjlite.web.interceptor.ControllerInvocation;
import cjlite.web.mvc.MethodParameter;

/**
 * @author YunYang
 * @version
 */
public final class InvocationUtils {

	public static <T> T getMethodParam(ControllerInvocation invocation, Class<T> t) {
		MethodParameter[] params = invocation.getPathMapping().getMethodParameters();

		int targetIndex = -1;
		for (int i = 0; i < invocation.getPathMapping().getMethodParametersLength(); i++) {
			MethodParameter param = params[i];
			if (param.getParamType().equals(t)) {
				targetIndex = i;
			}
		}
		if (targetIndex >= 0) {
			return (T) invocation.getMethodParam(targetIndex);
		}

		return null;
	}

	/**
	 * @param invocation
	 * @param class1
	 */
	public static <T> T getMethodParam(MethodInvocation invocation, Class<T> targetClass) {
		int paramCount = invocation.getMethod().getParameterCount();
		for (int i = 0; i < paramCount; i++) {
			Object p = invocation.getArguments()[i];
			if (targetClass.isAssignableFrom(p.getClass())) {
				return (T) p;
			}
		}
		return null;
	}

}
