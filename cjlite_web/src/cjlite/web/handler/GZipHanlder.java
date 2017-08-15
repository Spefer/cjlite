package cjlite.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author kevin
 * 
 */
public class GZipHanlder implements Handler {

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain handlerChain) {
		handlerChain.handle(request, response);
	}

	@Override
	public String getName() {
		return "GZip";
	}

	@Override
	public void initial() {
	}

}
