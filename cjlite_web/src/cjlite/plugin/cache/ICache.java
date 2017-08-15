package cjlite.plugin.cache;

import java.util.List;

public interface ICache {

	/**
	 * get cache element from cache object, <br>
	 * if not exist, return null
	 * 
	 * @param elementName
	 * @return
	 */
	CacheElement get(String name);

	/**
	 * get cache element from cache object, <br>
	 * if not exist, it would create a new cache element by give name
	 * 
	 * @param elementName
	 * @return
	 */
	CacheElement getOrCreate(String name);

	/**
	 * get cache element from cache object, <br>
	 * if not exist, it would create a new cache element by give name
	 * 
	 * @param name
	 * @param elementDesc
	 * @return
	 */
	CacheElement getOrCreate(String name, String elementDesc);

	/**
	 * is cacheElement exist
	 * 
	 * @param elementName
	 * @return
	 */
	boolean isExist(String elementName);

	/**
	 * remove the element by given name, then return removed one; <br>
	 * <code>null</code> if no mapping value by the elementName
	 * 
	 * @param elementName
	 * @return
	 */
	CacheElement remove(String elementName);

	/**
	 * @return cache elements keys
	 */
	List<String> getKeys();

	/**
	 * construct a new cache element object,<br>
	 * NOTE: this element is not exist in cache, you need to add it manualy
	 * 
	 * @param name
	 * @return
	 */
	public CacheElement newCacheElement(String name);

	/**
	 * construct a new cache element object,<br>
	 * <b>NOTE</b>: this element is not exist in cache, you need to add it manualy
	 * 
	 * @param name
	 * @param elementDesc
	 * @return
	 */
	public CacheElement newCacheElement(String name, String elementDesc);

	/**
	 * 
	 * Throw <b>IllegalArgumentException</b> if given name of elment already be exist
	 * 
	 * @param elment
	 * @see java.lang.IllegalArgumentException
	 * 
	 */
	public void put(CacheElement elment);

}
