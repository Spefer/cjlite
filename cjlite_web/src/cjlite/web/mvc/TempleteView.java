package cjlite.web.mvc;

public class TempleteView extends CharsetView {

	private String renderSource;

	TempleteView(String type, String contentType, String source) {
		super(type, contentType);
		this.renderSource = source;
	}

	public String getRenderSource() {
		return this.renderSource;
	}

}
