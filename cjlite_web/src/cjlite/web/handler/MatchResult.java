/**
 * 
 */
package cjlite.web.handler;

import cjlite.web.mvc.PathMapping;

/**
 * @author kevin
 * 
 */
final class MatchResult {

	private final boolean fixPath;
	private final PathMapping mapping;
	private final String requestMethod;
	private final PathVariables pathVariables;
	private final String requestPath;

	public MatchResult(PathMapping mapping, boolean fixPath, String requestPath, String requestMethod) {
		this(mapping, fixPath, requestPath, requestMethod, new PathVariables());
	}

	public MatchResult(PathMapping mapping, boolean fixPath, String requestPath, String method, PathVariables pv) {
		this.fixPath = fixPath;
		this.mapping = mapping;
		this.requestMethod = method;
		this.pathVariables = pv;
		this.requestPath = requestPath;
	}

	public PathMapping getPathMapping() {
		return this.mapping;
	}

	public PathVariables getPathVariables() {
		return this.pathVariables;
	}
	
	public String getRequestPath() {
		return this.requestPath;
	}

	public String getRequestMethod() {
		return this.requestMethod;
	}

}
