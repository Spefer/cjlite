/**
 * 
 */
package cjlite.web.mvc;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import cjlite.utils.Lists;
import cjlite.web.annotations.Path;
import cjlite.web.annotations.RequestMethod;
import cjlite.web.core.InterceptorCollector;
import cjlite.web.handler.UriPath;
import cjlite.web.interceptor.ControllerInterceptor;

/**
 * @author kevin
 * 
 */
public final class PathMapping {

	private final ControllerDefinition controllerDefinition;
	private final UriPath uriPath;
	private final String[] requestMethods;
	private final Method mappingMethod;
	private final MethodParameter[] methodParameters;
	private final InterceptorCollector inteceptorCollector;
	private List<Class<? extends ControllerInterceptor>> interceptorClassList;

	public PathMapping(ControllerDefinition cd, String pathValue, RequestMethod[] requestMethods, Method method,
			MethodParameter[] mps, InterceptorCollector inteceptorCollector) {
		this.controllerDefinition = cd;
		this.uriPath = new UriPath(pathValue, this.getPathText(method));
		this.mappingMethod = method;
		this.methodParameters = mps;
		this.requestMethods = resolveName(requestMethods);
		this.inteceptorCollector = inteceptorCollector;
		this.interceptorClassList = Lists.newArrayList();
		this.inteceptorCollector.collectWithParent(this.interceptorClassList);
	}

	/**
	 * Get Path text
	 * 
	 * @param method
	 * @return Path Text, return method name of no path exist or text of path is empty
	 */
	private String getPathText(Method method) {
		Path path = method.getAnnotation(Path.class);
		if (path == null) {
			return method.getName();
		}

		if (path.text().trim().length() == 0) {
			return method.getName();
		}

		return path.text();
	}

	private String[] resolveName(RequestMethod[] requestMethods) {
		if (requestMethods != null) {
			String[] rms = new String[requestMethods.length];
			for (int i = 0; i < requestMethods.length; i++) {
				rms[i] = requestMethods[i].name().toUpperCase();
			}
			return rms;
		}
		return null;
	}

	public boolean containMethod(String method) {
		if (requestMethods == null) {
			if (method.equalsIgnoreCase("get")) {
				return true;
			}
		} else {
			if (requestMethods.length == 0 && method.equalsIgnoreCase("get")) {
				return true;
			}

			for (int i = 0; i < this.requestMethods.length; i++) {
				if (this.requestMethods[i].equalsIgnoreCase(method)) {
					return true;
				}
			}
		}

		return false;
	}

	public ControllerDefinition getControllerDefinition() {
		return controllerDefinition;
	}

	public Method getMappingMethod() {
		return this.mappingMethod;
	}

	public MethodParameter[] getMethodParameters() {
		return methodParameters;
	}

	public int getMethodParametersLength() {
		if (methodParameters == null)
			return 0;
		return methodParameters.length;
	}

	public UriPath getUriPath() {
		return this.uriPath;
	}

	public String[] getRequestMethod() {
		return requestMethods;
	}

	public List<Class<? extends ControllerInterceptor>> getInterceptorClassList() {
		return interceptorClassList;
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UriPath: ").append(uriPath.path()).append("\n");
		builder.append("Request Method: ").append(Arrays.toString(this.requestMethods)).append("\n");
		builder.append("Controller Class: ").append(this.getMappingMethod().getDeclaringClass().getName()).append("\n");
		builder.append("Mapping Method: ").append(this.getMappingMethod().getName()).append("\n");
		return builder.toString();
	}
}
