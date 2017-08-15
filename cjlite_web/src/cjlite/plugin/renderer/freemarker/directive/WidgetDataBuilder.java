/**
 * 
 */
package cjlite.plugin.renderer.freemarker.directive;

import cjlite.web.mvc.Model;

/**
 * @author YunYang
 * @version Nov 20, 2015 3:26:48 PM
 */
public abstract class WidgetDataBuilder {
	private final Model widgetModel;
	private final WidgetData widgetData;

	public WidgetDataBuilder(Model model, String name) {
		this.widgetModel = Model.New();
		this.widgetData = new WidgetData(name);
		this.build(widgetModel);
		widgetModel.add(widgetData.getName(), this.widgetData);
		model.putAll(widgetModel);
	}

	public String getName() {
		return this.widgetData.getName();
	}

	public abstract void build(Model widgetModel);
}
