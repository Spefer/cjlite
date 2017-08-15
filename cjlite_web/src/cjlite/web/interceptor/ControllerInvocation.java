/**
 * 
 */
package cjlite.web.interceptor;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.web.handler.PathVariables;
import cjlite.web.mvc.PathMapping;

/**
 * The parameter of {@link cjlite.web.interceptor.ControllerInterceptor}
 * 
 * @author YunYang
 * 
 */
public interface ControllerInvocation {

	public Object invoke() throws Exception;

	/**
	 * @return Controller Object
	 */
	public Object getInvokeObject();

	/**
	 * HttpServletRequest, which should be the request for this controller mapping method
	 * 
	 * @return
	 */
	public HttpServletRequest getRequest();

	/**
	 * HttpServletResponse
	 * 
	 * @return
	 */
	public HttpServletResponse getResponse();

	/**
	 * the target method of path mapping
	 * 
	 * @return
	 */
	public Method getInvokeMethod();

	/**
	 * PathMapping current PathMapping Object
	 * 
	 * @return
	 */
	public PathMapping getPathMapping();

	/**
	 * Variables of this path
	 * 
	 * @return
	 */
	public PathVariables getPathVariables();

	/**
	 * get Method Parameter by given index
	 * 
	 * @param i
	 * @return
	 */
	public Object getMethodParam(int i);

}
