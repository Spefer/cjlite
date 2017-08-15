/**
 * 
 */
package cjlite.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kevin
 *
 */
public interface NotFoundHandler {

	void handle(HttpServletRequest request, HttpServletResponse response);
	
}
