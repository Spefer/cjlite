/**
 * 
 */
package cjlite.web.core;

import javax.inject.Inject;
import javax.inject.Provider;

import cjlite.config.Config;
import cjlite.web.statics.StaticResourceManager;

/**
 * @author kevin
 * 
 */
public class StaticResourceManagerProvider implements Provider<StaticResourceManager> {

	@Inject
	private Config config;
	
	@Override
	public StaticResourceManager get() {
		return new StaticResourceManagerImpl(config);
	}

}
