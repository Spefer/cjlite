/**
 * 
 */
package cjlite.app.core;

import java.util.List;
import java.util.Locale;

import com.google.inject.Binder;
import com.google.inject.Module;

import cjlite.app.AppBinder;
import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.plugin.Plugin;
import cjlite.utils.Lists;
import cjlite.utils.Strings;

/**
 * @author YunYang
 * @version Jun 24, 2015 10:37:04 AM
 */
public class AppBinderImpl implements AppBinder, Module {

	private static Logger logger = Logger.thisClass();

	protected static final String IllegalPluginClass_str = "The plugin class '{0}' you pass is not legal for new instance";

	protected final Config config;

	//
	private List<Class<? extends Plugin>> pluginClassList = Lists.newArrayList();

	private List<Plugin> pluginList = Lists.newArrayList();

	/**
	 * 
	 */
	public AppBinderImpl(Config config) {
		this.config = config;
	}

	public void configure(Binder binder) {
		bindPlugin(binder);
	}

	private void bindPlugin(Binder binder) {
		for (Plugin plugin : this.pluginList) {
			binder.install(plugin);
		}
	}

	/**
	 * Add Plugin class which must be extends from Plugin
	 * 
	 * @param pluginClass
	 */
	public void addPlugin(Class<? extends Plugin> pluginClass) {
		pluginClassList.add(pluginClass);
		try {
			Plugin plugin = pluginClass.newInstance();
			pluginList.add(plugin);
		} catch (InstantiationException | IllegalAccessException e) {
			String err = Strings.fillArgs(IllegalPluginClass_str, pluginClass.getName());
			logger.warn(err);
			throw new IllegalArgumentException(err, e);
		}
	}

	/**
	 * Add Plugin instance which must be extends from Plugin
	 * 
	 * @param pluginClass
	 */
	public void addPlugin(Plugin pluginInstance) {
		pluginList.add(pluginInstance);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see cjlite.app.AppBinder#setLocale(java.util.Locale)
	 */
	@Override
	public void setLocale(Locale locale) {
		this.config.setLocale(locale);
	}

}
