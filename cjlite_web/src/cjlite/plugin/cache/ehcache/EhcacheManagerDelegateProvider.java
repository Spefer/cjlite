/**
 * 
 */
package cjlite.plugin.cache.ehcache;

import javax.inject.Inject;
import javax.inject.Provider;

import cjlite.config.Config;
import cjlite.plugin.cache.CacheManager;
import cjlite.utils.FilePath;

/**
 * @author YunYang
 * @version Nov 12, 2015 1:44:47 PM
 */
public class EhcacheManagerDelegateProvider implements Provider<CacheManager> {

	private static final String WEB_INF = "/WEB-INF";

	private static final String EHCACHE_CONFIG_NAME = "ehcache_config";

	@Inject
	private Config config;

	@Override
	public CacheManager get() {
		String rootPath = this.config.getProperties("RootPath");
		String configFile = this.config.getProperties(EHCACHE_CONFIG_NAME);
		String ehcacheFileName = FilePath.join(rootPath, WEB_INF, configFile);
		EhcacheManagerDelegate em = new EhcacheManagerDelegate(ehcacheFileName);
		return em;
	}
}
