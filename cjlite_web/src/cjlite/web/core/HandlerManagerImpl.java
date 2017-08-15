/**
 * 
 */
package cjlite.web.core;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.web.handler.HandleResult;
import cjlite.web.handler.Handler;
import cjlite.web.handler.HandlerChain;
import cjlite.web.handler.HandlerManager;

/**
 * @author kevin
 * 
 */
class HandlerManagerImpl implements HandlerManager {

	private static final Logger logger = Logger.thisClass();

	private final Handler[] handlers;
	private final Config config;
	private boolean showException;

	HandlerManagerImpl(Config config, Handler[] handler) {
		this.config = config;
		this.handlers = handler;
		this.initial();
	}

	private void initial() {
		this.showException = this.config.getBool("show_exception", false);
	}

	@Override
	public HandleResult handle(HttpServletRequest request, HttpServletResponse response) {
		HandleResult result = new HandleResult(request, response);
		HandlerHttpServletResponseWrapper wrapperResp = new HandlerHttpServletResponseWrapper(response, result);
		try {
			new HandlerChain(this.handlers, result).handle(request, wrapperResp);
		} catch (Throwable t) {
			if (showException) {
				printException(t, request, response);
			} else {
				throw new RuntimeException(t.getMessage(), t.getCause());
			}
		}

		return result;
	}

	/**
	 * @param exception
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void printException(Throwable t, HttpServletRequest request, HttpServletResponse response) {
		response.reset();
		try {
			PrintWriter wirter = response.getWriter();
			t.printStackTrace(wirter);
			wirter.flush();
		} catch (IOException e) {
			logger.error("error on get writer from response");
		}
	}

}

class HandlerHttpServletResponseWrapper extends HttpServletResponseWrapper {

	private final HandleResult result;

	public HandlerHttpServletResponseWrapper(HttpServletResponse response, HandleResult result) {
		super(response);
		this.result = result;
	}

	@Override
	public PrintWriter getWriter() throws IOException {
		PrintWriter writer = new PrintWriter(super.getWriter());
		return writer;
	}
	
	

}