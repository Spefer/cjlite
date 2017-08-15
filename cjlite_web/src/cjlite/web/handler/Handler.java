/**
 * 
 */
package cjlite.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kevin
 * 
 */
public interface Handler {

	/**
	 * Handler Name
	 * 
	 * @return name
	 */
	public String getName();

	/**
	 * initialize method for this handler
	 */
	public void initial();

	/**
	 * Handle about request
	 * 
	 * @param request
	 * @param response
	 * @param handlerChain
	 */
	void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain handlerChain);

}
