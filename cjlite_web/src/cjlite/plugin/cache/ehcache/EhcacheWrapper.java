package cjlite.plugin.cache.ehcache;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.core.Ehcache;

import cjlite.plugin.cache.AbstractCache;
import cjlite.plugin.cache.CacheElement;

public class EhcacheWrapper extends AbstractCache {

	private EhcacheManagerDelegate ehcacheMgrDelegate;

	private CacheManager ehcacheMgr;

	private Cache<String, Object> cache;

	public EhcacheWrapper(EhcacheManagerDelegate ehcacheManager, CacheManager ehcache, String cacheName) {
		super(cacheName);
		this.ehcacheMgrDelegate = ehcacheManager;
		this.ehcacheMgr = ehcache;
		this.initial();
	}

	private void initial() {
		this.cache = this.ehcacheMgr.getCache(this.cacheName, String.class, Object.class);
		if (this.cache == null) {
			this.cache = this.ehcacheMgr.getCache(this.cacheName, String.class, Object.class);
		}
	}

	@Override
	public CacheElement newCacheElement(String name, String elementDesc) {
		return new EhcacheElement(this.ehcacheMgrDelegate, this.cache, name, elementDesc);
	}

	@Override
	public String toString() {
		return "EhcacheWrapper [ehcacheManager=" + ehcacheMgrDelegate + ", ehcache=" + ehcacheMgr + ", cache=" + cache
				+ ", cacheName=" + cacheName + "]";
	}

}
