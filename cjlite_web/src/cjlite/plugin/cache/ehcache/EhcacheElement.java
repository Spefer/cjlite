/**
 * 
 */
package cjlite.plugin.cache.ehcache;

import java.io.Serializable;

import org.ehcache.Cache;

import cjlite.app.UncheckedException;
import cjlite.plugin.cache.CacheElement;
import cjlite.plugin.cache.CacheEvent;
import cjlite.plugin.cache.RefreshType;
import cjlite.utils.Strings;

/**
 * @author YunYang
 * @version Nov 12, 2015 1:37:29 PM
 */
public class EhcacheElement implements CacheElement {

	private static final String noSerialImple = "The object's class[{0}] which you want to cache must implemented Serializable interface";

	@SuppressWarnings("unused")
	private final EhcacheManagerDelegate ehcacheManagerDelegate;

	private final String name;

	private final String description;

	private final Cache<String, Object> ehcache;

	public EhcacheElement(EhcacheManagerDelegate ehcacheManager, Cache<String, Object> cache, String name,
			String elementDesc) {
		this.ehcacheManagerDelegate = ehcacheManager;
		this.ehcache = cache;
		this.name = name;
		this.description = elementDesc;
	}

	@Override
	public void listen(CacheEvent event) {
		if (RefreshType.Clear == event.getType()) {
			this.ehcache.remove(name);
		}
	}

	@Override
	public Object getCachedValue() throws Throwable {
		Object element = this.ehcache.get(this.name);
		if (element == null) {
			return null;
		}
		return element;
	}

	@Override
	public void putCacheValue(Object object) {
		boolean serialized = object instanceof Serializable;
		if (!serialized) {
			String msg = Strings.fillArgs(noSerialImple, object.getClass().getName());
			throw new UncheckedException(msg);
		}
		this.ehcache.putIfAbsent(name, object);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "EhcacheElement [name=" + name + ", description=" + description + "]";
	}

}
