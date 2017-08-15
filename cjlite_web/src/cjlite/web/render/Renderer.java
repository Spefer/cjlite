/**
 * 
 */
package cjlite.web.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.web.mvc.ModelView;

/**
 * Renderer Interface,
 * 
 * @author kevin
 *
 */
public interface Renderer {

	/**
	 * initial this renderer
	 */
	public void initial();

	/**
	 * View type Name
	 * 
	 * @return
	 */
	public String getView();

	/**
	 * render the request by given model and view<br>
	 * View should be view in this render
	 * 
	 * @param modelView
	 * @param request
	 * @param response
	 * @throws RenderException
	 */
	public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
			throws RenderException;

}
