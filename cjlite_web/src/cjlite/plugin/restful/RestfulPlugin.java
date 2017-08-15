/**
 * 
 */
package cjlite.plugin.restful;

import com.google.inject.Binder;
import com.google.inject.matcher.Matchers;

import cjlite.plugin.Plugin;

/**
 * @author YunYang
 * @version
 */
public class RestfulPlugin implements Plugin {

	private static final String name = "RestfulPlugin";

	private static RestfulEnhancer enhancer = new RestfulEnhancer();

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void configure(Binder binder) {
		binder.requestInjection(enhancer);
		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(EntryRule.class), enhancer);
	}

}
