/**
 * 
 */
package cjlite.plugin.cache;

import java.util.List;

import cjlite.plugin.Shutdownable;

/**
 * @author YunYang
 * @version Oct 29, 2015 1:51:11 PM
 */
public interface CacheManager extends Shutdownable{

	/**
	 * fire a cache event
	 * 
	 * @param events
	 */
	void fireEvent(List<CacheEvent> events);

	/**
	 * return a cache by given name
	 * 
	 * @param cacheName
	 * @return
	 */
	ICache getCache(String cacheName);

	/**
	 * Construct a new cache instance by give name
	 * 
	 * @param cacheName
	 * @return
	 */
	ICache newCache(String cacheName);

	
}
