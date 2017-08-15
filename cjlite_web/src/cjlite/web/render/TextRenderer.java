/**
 * 
 */
package cjlite.web.render;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.log.Logger;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.Views;

/**
 * @author YunYang
 * @version
 */
public class TextRenderer extends AbstractRenderer {

	private static final Logger logger = Logger.thisClass();

	private static final String defaultEncoding = "UTF-8";

	private transient final String charset = ";charset=" + defaultEncoding;

	@Override
	public void initial() {

	}

	@Override
	public String getView() {
		return Views.Text;
	}

	@Override
	public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
			throws RenderException {
		try {
			String contentType = modelView.getView().getContentType() + charset;
			response.setContentType(contentType);
			response.setHeader("Pragma", "no-cache"); // HTTP/1.0 caches might not implement Cache-Control and might
														// only implement Pragma: no-cache
			response.setHeader("Cache-Control", "no-cache");
			response.setDateHeader("Expires", 0);
			PrintWriter pw = response.getWriter();
			pw.print(modelView.getModelMap().get(Views.Text));
			pw.flush();
		} catch (IOException e) {
			throw new RenderException("error on render plain text file data", e);
		}
	}

}
