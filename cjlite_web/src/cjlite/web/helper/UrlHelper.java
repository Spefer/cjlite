package cjlite.web.helper;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import cjlite.log.Logger;

public final class UrlHelper {
	private static final Logger logger = Logger.thisClass();

	public static String getRequestPath(HttpServletRequest request) {
		String cp = request.getContextPath();
		String uri = getRequestUri(request);
		if (uri.startsWith(cp)) {
			uri = uri.substring(cp.length());
		}

		int index = uri.indexOf('?');

		if (index < 0) {
			return uri;
		}

		return uri.substring(0, index);
	}

	public static String getRequestUri(HttpServletRequest request) {
		String uri = request.getRequestURI();
		try {
			return URLDecoder.decode(uri, "utf-8");
		} catch (UnsupportedEncodingException e) {
			return uri;
		}
	}

	@SuppressWarnings("unchecked")
	public static void printInfo(HttpServletRequest request) {
		Enumeration<String> headers = request.getHeaderNames();
		while (headers.hasMoreElements()) {
			String h = headers.nextElement();
			logger.debug("{0} = {1}", h, request.getHeader(h));
		}

	}
}
