/**
 * 
 */
package cjlite.plugin.renderer.freemarker.directive;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.ext.beans.BeanModel;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author YunYang
 * @version Nov 20, 2015 10:21:05 AM
 */
public class WidgetBlockDirective implements TemplateDirectiveModel {

	private static final String PARAM_NAME_NAME = "name";
	private final boolean debugInfo;

	public WidgetBlockDirective(boolean debugInfo) {
		this.debugInfo = debugInfo;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
			throws TemplateException, IOException {
		// ---------------------------------------------------------------------
		// 处理参数:
		String widgetParamName = "";

		Iterator paramIter = params.entrySet().iterator();
		while (paramIter.hasNext()) {
			Map.Entry ent = (Map.Entry) paramIter.next();

			String paramName = (String) ent.getKey();
			TemplateModel paramValue = (TemplateModel) ent.getValue();

			if (paramName.equals(PARAM_NAME_NAME)) {
				if (!(paramValue instanceof SimpleScalar)) {
					throw new TemplateModelException("The \"" + PARAM_NAME_NAME + "\" parameter " + "must be a string.");
				}
				widgetParamName = paramValue.toString();
			} else {
				throw new TemplateModelException("Unsupported parameter: " + paramName);
			}
		}

		if (loopVars.length > 1) {
			throw new TemplateModelException("At most one loop variable is allowed.");
		}

		String widgetName = WidgetData.prefixName + widgetParamName;

		TemplateHashModel data = env.getDataModel();
		TemplateModel model = data.get(widgetName);
		if (model == null || body == null) {
			return;
		}

		if (BeanModel.class.isInstance(model)) {
			BeanModel bm = (BeanModel) model;
			if (WidgetData.class.isInstance(bm.getWrappedObject())) {
				Writer out = env.getOut();
				this.showDebugInfo(out, widgetParamName, "start");
				body.render(out);
				this.showDebugInfo(out, widgetParamName, "end");
			}
		}

	}

	private void showDebugInfo(Writer out, String widgetName, String tag) throws IOException {
		if (!debugInfo) {
			return;
		}
		StringBuilder builder = new StringBuilder();
		builder.append("<!--  widget:").append(widgetName).append(" - ").append(tag).append(" -->");
		out.write(builder.toString());
	}
}
