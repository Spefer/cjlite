/**
 * 
 */
package cjlite.web.render;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.Views;


/**
 * @author YunYang
 * @version 
 */
public class ResponseViewRenderer extends AbstractRenderer {

	@Override
	public void initial() {

	}

	@Override
	public String getView() {
		return Views.ResponseView;
	}

	@Override
	public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
			throws RenderException {

	}

}
