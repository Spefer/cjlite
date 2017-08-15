package cjlite.web.statics;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.web.handler.HandleResult;

public interface StaticResourceManager {

	public HandleResult lookup(HttpServletRequest request, HttpServletResponse response);

}
