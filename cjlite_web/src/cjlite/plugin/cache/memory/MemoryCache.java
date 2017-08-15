/**
 * 
 */
package cjlite.plugin.cache.memory;

import cjlite.plugin.cache.AbstractCache;
import cjlite.plugin.cache.CacheElement;

/**
 * @author YunYang
 * @version Nov 17, 2015 2:33:32 PM
 */
public class MemoryCache extends AbstractCache {

	public MemoryCache(String cacheName) {
		super(cacheName);
	}

	@Override
	public CacheElement newCacheElement(String name, String elementDesc) {
		return new MemoryCacheElement(name, elementDesc);
	}

}
