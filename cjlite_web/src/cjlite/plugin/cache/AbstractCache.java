/**
 * 
 */
package cjlite.plugin.cache;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import cjlite.utils.Lists;

/**
 * @author YunYang
 * @version Nov 17, 2015 2:38:03 PM
 */
public abstract class AbstractCache implements ICache {

	private ConcurrentMap<String, CacheElement> cacheElementMap = new ConcurrentHashMap<String, CacheElement>();

	protected final String cacheName;

	public AbstractCache(String cacheName) {
		this.cacheName = cacheName;
	}

	@Override
	public CacheElement get(String name) {
		return cacheElementMap.get(name);
	}

	@Override
	public CacheElement getOrCreate(String name) {
		CacheElement element = cacheElementMap.get(name);
		if (element == null) {
			element = this.newCacheElement(name);
			cacheElementMap.putIfAbsent(name, element);
		}
		return element;
	}

	@Override
	public CacheElement getOrCreate(String name, String elementDesc) {
		CacheElement element = cacheElementMap.get(name);
		if (element == null) {
			element = this.newCacheElement(name, elementDesc);
			cacheElementMap.putIfAbsent(name, element);
		}
		return element;
	}

	@Override
	public List<String> getKeys() {
		List<String> keys = Lists.newArrayList();
		keys.addAll(cacheElementMap.keySet());
		return keys;
	}

	@Override
	public boolean isExist(String elementName) {
		return cacheElementMap.containsKey(elementName);
	}

	@Override
	public CacheElement remove(String elementName) {
		return cacheElementMap.remove(elementName);
	}

	@Override
	public CacheElement newCacheElement(String name) {
		return this.newCacheElement(name, "");
	}

	@Override
	public void put(CacheElement elment) {
		if (cacheElementMap.containsKey(elment.getName())) {
			throw new IllegalArgumentException("CacheElement[" + elment.getName()
					+ "]already exist, you can not construct new CacheElement instance,except remove first");
		}

		cacheElementMap.putIfAbsent(elment.getName(), elment);
	}

	/**
	 * @return the cacheName
	 */
	public String getCacheName() {
		return cacheName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AbstractCache [cacheName=").append(cacheName);
		builder.append(", elmentCount=").append(cacheElementMap.size());
		builder.append("]");
		return builder.toString();
	}

}
