package cjlite.web.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.log.Logger;
import cjlite.utils.Strings;
import cjlite.web.NotFoundHandler;
import cjlite.web.helper.UrlHelper;

public class NotFoundHandlerImpl implements NotFoundHandler {

	private static final Logger logger = Logger.thisClass();

	private static final String htmldata = "<html><head><title>{0}</title></head><body>{1}</body></html>";
	private static final String pagetitle = "Resource:{0} not found on server";
	private static final String bodytext = "Resource:{0} not found on server<br>Please try back to homepage";

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response) {
		String requestUrl = UrlHelper.getRequestPath(request);
		String title = Strings.fillArgs(pagetitle, requestUrl);
		String body = Strings.fillArgs(bodytext, requestUrl);
		String html = Strings.fillArgs(htmldata, title, body);
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
			writer.write(html);
			writer.flush();
		} catch (IOException e) {
			logger.error("error on process not found page", e);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
