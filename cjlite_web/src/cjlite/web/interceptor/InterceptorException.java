/**
 * 
 */
package cjlite.web.interceptor;

import cjlite.web.handler.HandleException;

/**
 * @author YunYang
 * @version Jul 3, 2015 10:07:47 AM
 */
public class InterceptorException extends HandleException {
	private static final long serialVersionUID = 1L;

	public InterceptorException(String msg, Exception e) {
		super(msg, e);
	}

}
