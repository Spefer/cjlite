package cjlite.web.handler;

public class UriPath {

	private final String path;
	private String text;
	private int hashCode;
	private UriMatcher uriMatcher;


	public UriPath(String pathValue) {
		this(pathValue, "");
	}


	public UriPath(String pathValue, String text) {
		this(pathValue, text, true);
	}


	public UriPath(String pathValue, boolean parseUri) {
		this(pathValue, "", parseUri);
	}


	public UriPath(String pathValue, String text, boolean parseUri) {
		if (pathValue.startsWith("/")) {
			this.path = pathValue;
		} else {
			this.path = "/" + pathValue;
		}
		this.text = text;
		this.computeHashCode();
		if (parseUri) {
			uriMatcher = new UriMatcher(this.path);
		}
	}


	private void computeHashCode() {
		final int prime = 31;
		int result = 1;
		hashCode = prime * result + ((path == null) ? 0 : path.hashCode());
	}


	@Override
	public int hashCode() {
		return hashCode;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UriPath other = (UriPath) obj;
		if (hashCode != other.hashCode)
			return false;
		return true;
	}


	public boolean match(String requestUri, PathVariables pathVariables) {
		if (uriMatcher == null) {
			uriMatcher = new UriMatcher(path);
		}

		if (uriMatcher.isStatic()) {
			return this.path.equalsIgnoreCase(requestUri);
		}

		if (uriMatcher.match(requestUri, pathVariables)) {
			return true;
		}

		return false;
	}


	public boolean isStaticPath() {
		return uriMatcher.isStatic();
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("UriPath [path=");
		builder.append(path);
		builder.append(", hashCode=");
		builder.append(hashCode);
		builder.append("]");
		return builder.toString();
	}


	public String path() {
		return path;
	}


	public String text() {
		return this.text;
	}

}
