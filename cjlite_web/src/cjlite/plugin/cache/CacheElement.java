/**
 * 
 */
package cjlite.plugin.cache;

/**
 * @author YunYang
 * @version Oct 29, 2015 2:00:08 PM
 */
public interface CacheElement extends CacheEventListener {

	public Object getCachedValue() throws Throwable;

	public void putCacheValue(Object object);

	public String getName();

}
