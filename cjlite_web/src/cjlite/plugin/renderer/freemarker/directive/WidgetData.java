package cjlite.plugin.renderer.freemarker.directive;

public final class WidgetData {

	static final String prefixName = "WidgetData.";

	private final String name;

	public WidgetData(String name) {
		this.name = prefixName + name;
	}

	@Override
	public String toString() {
		return "WidgetData [name=" + name + "]";
	}

	public String getName() {
		return name;
	}

}
