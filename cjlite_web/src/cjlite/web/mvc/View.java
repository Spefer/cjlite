package cjlite.web.mvc;

public class View {

	protected static final String Charset_str = "; charset=";

	/**
	 * we need this type to find corresponding renderer
	 */
	protected String type;

	/**
	 * we need this to renderer content to response which flow into client
	 */
	protected String contentType;

	public View(String _type, String contentType) {
		this.type = _type;
		this.contentType = contentType;
	}

	public String getType() {
		return this.type;
	}

	public String getContentType() {
		return this.contentType;
	}

}
