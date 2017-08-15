/**
 * 
 */
package cjlite.web.interceptor;

/**
 * @author kevin
 * 
 */
public interface ControllerInterceptor {
	/**
	 * @param invocation
	 * @return
	 */
	public Object intercept(ControllerInvocation invocation) throws Exception;
}
