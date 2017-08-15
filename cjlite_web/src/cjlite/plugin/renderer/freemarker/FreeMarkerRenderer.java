/**
 * 
 */
package cjlite.plugin.renderer.freemarker;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.plugin.renderer.freemarker.directive.WidgetBlockDirective;
import cjlite.utils.Strings;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.TempleteView;
import cjlite.web.mvc.Views;
import cjlite.web.render.AbstractRenderer;
import cjlite.web.render.RenderException;
import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.cache.WebappTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.TemplateModelException;

/**
 * @author kevin
 * 
 */
@SuppressWarnings("unused")
public class FreeMarkerRenderer extends AbstractRenderer {

	private static final Logger logger = Logger.thisClass();

	private static String default_InputEncoding = "UTF-8";

	private static String default_OutputEncoding = "UTF-8";

	private transient final String contentType = "text/html; charset=" + default_OutputEncoding;

	/**
	 * Default template folder for freemarker
	 */
	private static final String default_templFolder = "/WEB-INF/templs";

	private static final String freemarkerTemplateMacroPrefixKey = "freemarker.templete.macros";

	/**
	 * configuration key in config file
	 */
	private static final String templateClasspathKey = "freemarker.templete.classpath";

	private static final String templateClasspathDefaultFolder = "/templs";

	private static final String[] htmlContentTypes = new String[] { ".html", ".htm", ".ftl", ".templ" };

	@Inject
	private Config config;

	@Inject
	private Provider<ServletContext> servletContextProvider;

	//
	private Configuration freemarkerCfg;

	private String defaultEncoding;

	private String outputEncoding;

	@Override
	public void render(ModelView modelView, HttpServletRequest request, HttpServletResponse response)
			throws RenderException {
		TempleteView view = (TempleteView) modelView.getView();
		view.updateCharset(outputEncoding);
		String contentType = this.parseContentType(view, request);
		response.setContentType(contentType);

		Enumeration<String> attrs = request.getAttributeNames();

		Map<String, Object> root = new HashMap<String, Object>();
		while (attrs.hasMoreElements()) {
			String attrName = attrs.nextElement();
			root.put(attrName, request.getAttribute(attrName));
		}
		root.putAll(modelView.getModelMap());

		PrintWriter writer = null;
		try {
			Template template = this.freemarkerCfg.getTemplate(view.getRenderSource());
			writer = response.getWriter();
			template.process(root, writer); // Merge the data-model and the template
			writer.close();
		} catch (IOException e) {
			String errorMsg = Strings.fillArgs("error on render request, because: {0}", e.getMessage());
			throw new RenderException(errorMsg, e);
		} catch (TemplateException e) {
			String errorMsg = Strings.fillArgs("error on render request, because: {0}", e.getMessage());
			throw new RenderException(errorMsg, e);
		} catch (Exception e) {
			logger.error("error on render", e);
			// throw e;
		} finally {
			// if (writer != null)
			// writer.close();
		}
	}

	private String parseContentType(TempleteView view, HttpServletRequest request) {
		String source = view.getRenderSource().toLowerCase();

		for (String type : htmlContentTypes) {
			if (source.endsWith(type)) {
				return view.getContentType();
			}
		}

		String targetType = request.getSession().getServletContext().getMimeType(source);
		return targetType;
	}

	@Override
	public String getView() {
		return Views.FreeMarker;
	}

	@Override
	public void initial() {
		freemarkerCfg = new Configuration();
		freemarkerCfg.setTemplateLoader(this.getTemplaterLoaders());

		if ("DEVELOPMENT".equalsIgnoreCase(config.getProperties("Stage"))) {
			freemarkerCfg.setTemplateUpdateDelay(0);
		} else {
			int delay = config.getInt("freemarker.templete.loaddelay", 0);
			freemarkerCfg.setTemplateUpdateDelay(delay);
		}

		freemarkerCfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		freemarkerCfg.setLocalizedLookup(false);
		freemarkerCfg.setWhitespaceStripping(true);
		freemarkerCfg.setAutoImports(this.getAutoImportMap());
		defaultEncoding = config.getProperties("freemarker.templete.defaultEncoding", default_InputEncoding);
		freemarkerCfg.setDefaultEncoding(defaultEncoding);
		outputEncoding = config.getProperties("freemarker.templete.outputEncoding", default_OutputEncoding);
		freemarkerCfg.setOutputEncoding(outputEncoding);
		freemarkerCfg.setNumberFormat("0");
		this.addSharedVaiables(freemarkerCfg);
	}

	private void addSharedVaiables(Configuration _freemarkerCfg) {
		try {
			_freemarkerCfg.setSharedVariable("CP", this.config.getProperties("contextpath"));
			_freemarkerCfg.setSharedVariable("widget", new WidgetBlockDirective(true));
		} catch (TemplateModelException e) {
			logger.error("error on add share vaiables to freemarker", e);
		}
	}

	private Map<String, String> getAutoImportMap() {
		Map<String, String> importMap = new HashMap<String, String>();
		importMap.put("page", "ftl_library.ftl");

		Properties props = this.config.getPropsPrefixStartWith(freemarkerTemplateMacroPrefixKey);

		int startIdx = freemarkerTemplateMacroPrefixKey.length() + 1;
		for (String key : props.stringPropertyNames()) {
			String macroName = key.substring(startIdx);
			String macroValue = props.getProperty(key);
			importMap.put(macroName, macroValue);
		}

		return importMap;
	}

	private TemplateLoader getTemplaterLoaders() {
		TemplateLoader[] loaders = new TemplateLoader[2];
		// load template from root of class path
		String classFolder = this.config.getProperties(this.templateClasspathKey, this.templateClasspathDefaultFolder);
		loaders[0] = new ClassTemplateLoader(FreeMarkerRenderer.class, classFolder);
		String templateFolder = config.getProperties("freemarker.templete.folder", default_templFolder);
		loaders[1] = new WebappTemplateLoader(servletContextProvider.get(), templateFolder);
		return new MultiTemplateLoader(loaders);
	}

}
