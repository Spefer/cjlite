/**
 * 
 */
package cjlite.app;

/**
 * @author kevin
 *
 */
public interface CjliteApp {

	/**
	 * @return name of this CjliteApp
	 */
	String getName();

	/**
	 * Get Instance from APP Container
	 * 
	 * @param instanceClass
	 * @return
	 */
	<T> T getInstance(Class<T> instanceClass);

	/**
	 * 
	 */
	void destroy();

}
