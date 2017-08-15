package cjlite.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class HandlerChain {

	private Handler[] handlers;
	private HandleResult result;

	private int index = -1;

	public HandlerChain(Handler[] handlers, HandleResult result) {
		this.handlers = handlers;
		this.result = result;
	}

	public void handle(HttpServletRequest request, HttpServletResponse response) {
		this.index += 1;
		if (handlers != null) {
			if (this.index < handlers.length) {
				Handler handler = handlers[this.index];
				handler.handle(request, response, this);
			}
		}
	}

}
