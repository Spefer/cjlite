/**
 * 
 */
package cjlite.web.request;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * @author YunYang
 * @version
 */
public class RequestDebugWrapper implements InvocationHandler {

	// private static final Logger logger = Logger.thisClass();

	private static final String getHeaderMethodName = "getHeader";

	private static final Map<String, Object> debugValueMap = new HashMap<String, Object>() {

		{
			this.put("If-Modified-Since", -1l);
			this.put("If-None-Match", "W/\"\"");
		}
	};

	private HttpServletRequest request;

	/**
	 * @param request
	 */
	public RequestDebugWrapper(HttpServletRequest request) {
		this.request = request;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object value = method.invoke(request, args);
		// logger.debug("methodName = {0},params = {1}, value = {2}", method.getName(), Arrays.toString(args),
		// String.valueOf(value));
		if (getHeaderMethodName.equals(method.getName())) {
			if (args.length == 1 && debugValueMap.containsKey(args[0]))
				value = debugValueMap.get(args[0]);
		}
		return value;
	}

	/**
	 * @param request
	 * @return
	 */
	public static HttpServletRequest wrap(HttpServletRequest request) {
		return (HttpServletRequest) Proxy.newProxyInstance(request.getClass().getClassLoader(),
				request.getClass().getInterfaces(), new RequestDebugWrapper(request));
	}

}
