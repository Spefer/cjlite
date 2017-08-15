/**
 * 
 */
package cjlite.web.render;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.log.Logger;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.RedirectView;
import cjlite.web.mvc.Views;

/**
 * @author kevin
 * 
 */
public class RedirectRenderer implements Renderer {

	private static final Logger logger = Logger.thisClass();

	@Override
	public void initial() {
	}

	@Override
	public String getView() {
		return Views.Redirect;
	}

	@Override
	public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
			throws RenderException

	{
		RedirectView view = (RedirectView) modelView.getView();
		String url = view.getRedirectUrl();
		try {
			String contextPath = request.getContextPath();
			String newRediect = contextPath + url;
			response.sendRedirect(newRediect);
		} catch (IOException e) {
			logger.error("error on send redirect url'{0}'", url);

		}
	}
}
