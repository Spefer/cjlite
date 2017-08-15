package cjlite.web.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public final class HandleResult {
	private final HttpServletRequest request;
	private final HttpServletResponse response;

	public HandleResult(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
	}

	public boolean isProcessed() {
		return this.response.isCommitted();
	}

}
