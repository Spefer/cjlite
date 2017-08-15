/**
 * 
 */
package cjlite.plugin.cache.memory;

import cjlite.plugin.cache.AbstractCacheManager;
import cjlite.plugin.cache.CacheManager;
import cjlite.plugin.cache.ICache;

/**
 * @author YunYang
 * @version Oct 29, 2015 4:22:27 PM
 */
public final class MemoryCacheManager extends AbstractCacheManager implements CacheManager {

	public ICache addCache(String cacheName) {
		return new MemoryCache(cacheName);
	}

	@Override
	protected ICache buildCache(String cacheName) {
		return new MemoryCache(cacheName);
	}

	@Override
	public void shutdown() {
	}

}
