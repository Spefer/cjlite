package cjlite.web.mvc;

class CharsetView extends View {

	private String charset;

	CharsetView(String type, String contentType) {
		super(type, contentType);
	}

	public CharsetView(String type, String contentType, String charset) {
		super(type, contentType);
		this.charset = charset;
	}

	public void updateCharset(String charset) {
		this.charset = charset;
	}

	@Override
	public String getContentType() {
		return this.contentType + Charset_str + charset;
	}

}
