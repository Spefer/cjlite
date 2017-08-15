/**
 * 
 */
package cjlite.app;

/**
 * <pre>
 * This is not for Web App config type
 * For web config: see: {@link cjlite.web.WebConfig}
 * </pre>
 * @author YunYang
 * @version Jun 24, 2015 11:13:03 AM
 */
public interface AppConfig {
	/**
	 * @param binder
	 */
	public void config(AppBinder binder);
}
