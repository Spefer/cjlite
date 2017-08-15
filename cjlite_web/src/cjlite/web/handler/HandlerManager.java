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
public interface HandlerManager {

	HandleResult handle(HttpServletRequest request, HttpServletResponse response);

}
