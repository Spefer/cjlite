/**
 * 
 */
package cjlite.plugin.restful;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import cjlite.log.Logger;
import cjlite.utils.Lists;
import cjlite.utils.Strings;
import cjlite.web.annotations.Controller;
import cjlite.web.annotations.Path;
import cjlite.web.handler.UriPath;
import cjlite.web.mvc.ControllerDefinition;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.PathMapping;
import cjlite.web.mvc.RequestContext;

/**
 * @author YunYang
 * @version
 */
@Controller
@Path(RestfulConfig.EntriesRoute)
public class RestfuleEntriesController {

	private static final Logger logger = Logger.thisClass();

	private static final String htmlFileName = "entry_list.html";

	//
	private static final TypeLiteral<ControllerDefinition> controllerType = TypeLiteral.get(ControllerDefinition.class);

	Injector injector;

	private boolean initialized = false;

	private List<RestfulEntryDefinition> entryList;

	private StringBuilder htmlBuilder = new StringBuilder();

	@Inject
	public void setInjector(Injector injector) {
		this.injector = injector;
		this.initial();
		initialized = true;
	}

	/**
	 * 
	 */
	private void initial() {
		if (initialized) {
			return;
		}
		//
		entryList = Lists.newArrayList();
		List<Binding<ControllerDefinition>> cdlist = this.injector.findBindingsByType(controllerType);
		cdlist.forEach(e -> {
			ControllerDefinition cd = e.getProvider().get();
			if (RestfulEntries.class.isAssignableFrom(cd.getControllerClass())) {
				this.parseEntry(cd.getStaticPathMappingMap());
				this.parseEntry(cd.getParamPathMappingMap());
			}
		});

		htmlBuilder = new StringBuilder();
		this.readHtmlFile();
		this.renderHtml();
	}

	/**
	 * 
	 */
	private void renderHtml() {
		StringBuilder b = new StringBuilder();
		this.entryList.forEach(e -> {
			b.append(e.toHtml());
		});

		htmlBuilder = new StringBuilder(Strings.fillArgs(htmlBuilder.toString(), b.toString()));
	}

	/**
	 * 
	 */
	private void readHtmlFile() {
		InputStream is = RestfuleEntriesController.class.getResourceAsStream(htmlFileName);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				htmlBuilder.append(line).append("\n");
			}
		} catch (IOException e) {
			logger.error("error on read html data from [{0}]", 3, htmlFileName);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
			}
		}
	}

	/**
	 * @param paramPathMappingMap
	 */
	private void parseEntry(Map<UriPath, Map<String, PathMapping>> mappingMap) {
		mappingMap.forEach((k, map) -> {
			map.forEach((m, entry) -> {
				EntryRule rule = entry.getMappingMethod().getAnnotation(EntryRule.class);
				entryList.add(new RestfulEntryDefinition(k, m, rule));
			});
		});
	}

	public ModelView index(RequestContext context) {
		return ModelView.HtmlTextView(htmlBuilder.toString());
	}

}
