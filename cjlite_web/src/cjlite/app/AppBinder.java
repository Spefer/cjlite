/**
 * 
 */
package cjlite.app;

import java.util.Locale;

import cjlite.plugin.Plugin;

/**
 * @author YunYang
 * @version Jun 24, 2015 10:31:03 AM
 */
public interface AppBinder {

	/**
	 * Add Plugin class which must be extends from Plugin
	 * 
	 * @param pluginClass
	 */
	public void addPlugin(Class<? extends Plugin> pluginClass);

	/**
	 * Set locale which we would use in this framework
	 * 
	 * @param locale
	 */
	public void setLocale(Locale locale);
}
