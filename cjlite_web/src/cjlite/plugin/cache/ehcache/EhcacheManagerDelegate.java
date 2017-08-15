/**
 * 
 */
package cjlite.plugin.cache.ehcache;

import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import cjlite.log.Logger;
import cjlite.plugin.cache.AbstractCacheManager;
import cjlite.plugin.cache.ICache;
import cjlite.utils.Strings;

/**
 * @author YunYang
 * @version Nov 12, 2015 1:36:14 PM
 */
public class EhcacheManagerDelegate extends AbstractCacheManager {

	private static final Logger logger = Logger.thisClass();

	private final String ehcacheFileName;

	private CacheManager cacheManager;

	public EhcacheManagerDelegate(String ehcacheFileName) {
		this.ehcacheFileName = ehcacheFileName;
		this.initialize();
	}

	private void initialize() {
		try {

			cacheManager = CacheManagerBuilder
					.newCacheManagerBuilder().withCache("preConfigured", CacheConfigurationBuilder
							.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)))
					.build();
			cacheManager.init();

			// this.ehcacheMgr = CacheManagerBuilder.newCacheManager(this.ehcacheFileName);
		} catch (Exception e) {
			String error = Strings.fillArgs("error in create EhcacheManager by file:{0},\nbecause:",
					this.ehcacheFileName, e.getMessage());
			logger.warn(error, e);
			cacheManager = CacheManagerBuilder
					.newCacheManagerBuilder().withCache("preConfigured", CacheConfigurationBuilder
							.newCacheConfigurationBuilder(Long.class, String.class, ResourcePoolsBuilder.heap(10)))
					.build();
			cacheManager.init();
		}
	}

	@Override
	protected ICache buildCache(String cacheName) {
		return new EhcacheWrapper(this, cacheManager, cacheName);
	}

	@Override
	public void shutdown() {
		cacheManager.removeCache("preConfigured");
	}

}
