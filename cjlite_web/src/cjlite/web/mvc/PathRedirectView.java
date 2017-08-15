package cjlite.web.mvc;

import cjlite.web.annotations.RequestMethod;

/**
 * This is a internal Controller Path redirect, it would carry model into next path mapping method
 * 
 * @author YunYang
 * @version
 */
public class PathRedirectView extends View {

	private final String redirectControllerPath;

	private final RequestMethod method;

	public PathRedirectView(String _type, RequestMethod method, String redirectControllerPath) {
		super(_type, "");
		this.redirectControllerPath = redirectControllerPath;
		this.method = method;
	}

	public String getRedirectControllerPath() {
		return this.redirectControllerPath;
	}

	public RequestMethod getMethod() {
		return this.method;
	}

}
