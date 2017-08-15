/**
 * 
 */
package cjlite.plugin.restful;

import cjlite.web.WebBinder;
import cjlite.web.WebConfig;

/**
 * @author YunYang
 * @version
 */
public class RestfulConfig implements WebConfig {

	public static final String EntriesRoute = "/restful/api/entries";

	private boolean debug;

	public RestfulConfig(boolean debug) {
		this.debug = debug;
	}

	@Override
	public void config(WebBinder binder) {
		if (this.debug) {
			binder.addController(RestfuleEntriesController.class);
		}
		binder.addPlugin(RestfulPlugin.class);
	}

}
