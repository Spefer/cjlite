/**
 * 
 */
package cjlite.app.core;

import java.util.Map;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.Key;

import cjlite.app.CjliteApp;
import cjlite.config.Config;
import cjlite.plugin.Shutdownable;

/**
 * @author kevin
 * 
 */
class CjliteAppImpl implements CjliteApp {

	private static final String defaultName = "CjliteApp.Default";

	private final String name;

	private final Injector injector;

	private final ConfigImpl config;

	private final String stage;

	CjliteAppImpl(String stage, Injector injector, ConfigImpl config) {
		this(defaultName, stage, injector, config);
	}

	CjliteAppImpl(String name, String stage, Injector injector, ConfigImpl config) {
		this.name = name;
		this.stage = stage;
		this.injector = injector;
		this.config = config;
	}

	public <T> T getInstance(Class<T> instanceClass) {
		return injector.getInstance(instanceClass);
	}

	@Override
	public void destroy() {
		Map<Key<?>, Binding<?>> allBindings = injector.getAllBindings();
		for (Key<?> key : allBindings.keySet()) {
			if (Shutdownable.class.isAssignableFrom(key.getTypeLiteral().getRawType())) {
				Binding<?> binding = allBindings.get(key);
				Object result = binding.getProvider().get();
				if (Shutdownable.class.isInstance(result)) {
					Shutdownable sdable = (Shutdownable) result;
					sdable.shutdown();
				}
			}
		}
	}

	public Config getConfig() {
		return this.config;
	}

	@Override
	public String getName() {
		return this.name;
	}

}
