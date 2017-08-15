package cjlite.plugin.cache.memory;

import cjlite.plugin.cache.CacheElement;
import cjlite.plugin.cache.CacheEvent;
import cjlite.plugin.cache.RefreshType;

public final class MemoryCacheElement implements CacheElement {
	private final String name;
	private final String description;

	private Object cacheValue;

	public MemoryCacheElement(String name) {
		this(name, "");
	}

	public MemoryCacheElement(String name, String elementDesc) {
		this.name = name;
		this.description = elementDesc;
	}

	@Override
	public Object getCachedValue() throws Throwable {
		return this.cacheValue;
	}

	@Override
	public void putCacheValue(Object object) {
		this.cacheValue = object;
	}

	@Override
	public void listen(CacheEvent event) {
		if (RefreshType.Clear == event.getType()) {
			this.cacheValue = null;
		}
	}

	@Override
	public String getName() {
		return this.name;
	}

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "MemoryCacheElement [name=" + name + ", description=" + description + "]";
	}

}