package cjlite.web.mvc;

import cjlite.web.annotations.RequestMethod;

public final class Views {

	public static final String htmlContentType = "text/html";
	public static final String textContentType = "text/plain";

	public static final String FreeMarker = "FreeMarker";
	public static final String Velocity = "Velocity";
	public static final String Text = "Text";
	public static final String Jsp = "Jsp";
	public static final String Redirect = "Redirect";
	public static final String Json = "Json";
	public static final String PathRedirect = "PathRedirect";
	
	//
	public static final String ResponseView = "ResponseView";
	
	
	public static View FreeMarkerView(String fileName) {
		return new TempleteView(FreeMarker, htmlContentType, fileName);
	}

	public static View JsonView(String fileName) {
		return new View(Json, htmlContentType);
	}

	public static View TextView() {
		return new CharsetView(Text, textContentType);
	}

	public static View redirectView(String redirectUrl) {
		return new RedirectView(Redirect, redirectUrl);
	}

	public static View pathRedirect(String pathRedirectUrl, RequestMethod method) {
		return new PathRedirectView(PathRedirect, method, pathRedirectUrl);
	}
	
	public static View ResponseView() {
		return new View(ResponseView, ResponseView);
	}


}
