/**
 * 
 */
package cjlite.app.core;

import cjlite.config.Config;

import com.google.inject.Binder;
import com.google.inject.Module;

/**
 * @author YunYang
 * @version Jun 24, 2015 10:49:35 AM
 */
public class AppModule implements Module {

	private final Config config;

	public AppModule(Config config) {
		this.config = config;
	}

	public void configure(Binder binder) {
		binder.bind(Config.class).toInstance(config);
		webConfigure(binder);
	}

	/**
	 * @param binder
	 */
	protected void webConfigure(Binder binder) {
	}

}
