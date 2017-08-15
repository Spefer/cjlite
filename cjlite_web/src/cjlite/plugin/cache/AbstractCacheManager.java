/**
 * 
 */
package cjlite.plugin.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cjlite.log.Logger;

/**
 * @author YunYang
 * @version Oct 29, 2015 4:39:04 PM
 */
public abstract class AbstractCacheManager implements CacheManager {
	private static final Logger logger = Logger.thisClass();

	private ConcurrentMap<String, ICache> cacheMap = new ConcurrentHashMap<String, ICache>();
	private CacheEventProcessor eventProcessor;

	public AbstractCacheManager() {
		eventProcessor = new CacheEventProcessor(this);
	}

	@Override
	public final ICache getCache(String cacheName) {
		ICache cache = cacheMap.get(cacheName);
		if (cache == null) {
			logger.warn("The cache for name[{0}] is not exist", cacheName);
		}
		return cache;
	}

	@Override
	public final ICache newCache(String cacheName) {
		ICache cache = this.buildCache(cacheName);
		cacheMap.putIfAbsent(cacheName, cache);
		return cache;
	}

	protected abstract ICache buildCache(String cacheName);

	@Override
	public final void fireEvent(List<CacheEvent> events) {
		eventProcessor.fireEvent(events);
	}

	public final List<CacheElement> getCacheElements(String name, NameMatcher matcher) {
		List<CacheElement> cacheEntryList = new ArrayList<CacheElement>();

		for (ICache cache : cacheMap.values()) {
			List<String> keys = cache.getKeys();
			for (String key : keys) {
				if (matcher.isNameMatch(name, key)) {
					cacheEntryList.add(cache.get(key));
				}
			}
		}

		return cacheEntryList;
	}

}
