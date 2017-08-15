/**
 * 
 */
package cjlite.web;

import cjlite.app.AppBinder;
import cjlite.web.handler.Handler;
import cjlite.web.interceptor.ControllerInterceptor;

/**
 * @author kevin
 * 
 */
public interface WebBinder extends AppBinder {

	/**
	 * Add Hanlder class which must be extends from Handler
	 * 
	 * @param handlerClass
	 */
	public void addHandler(Class<? extends Handler> handlerClass);

	/**
	 * Add controller package, system would scan classes with Controller annotation under this package and subpackage
	 * 
	 * @param controllerPackage
	 */
	public void addControllerPackage(Package controllerPackage);

	/**
	 * Add a controllerClass which must have type Controller annotation
	 * 
	 * @param controllerClass
	 */
	public void addController(Class<?> controllerClass);

	/**
	 * Add interceptor class
	 * 
	 * @param interceptorClass
	 */
	public void addInterceptor(Class<? extends ControllerInterceptor> interceptorClass);

	/**
	 * Install webConfig into this binder
	 * 
	 * @param webConfig
	 */
	public void install(WebConfig webConfig);

	/**
	 * Scan WebConfig classes by given package
	 * 
	 * @param configPackage
	 */
	public void scanConfigPackage(Package configPackage);

	/**
	 * Set the static resources folder in web root, try to use <code>WebRoot().statics(staticPaths[0]);</code>
	 * 
	 * @param string
	 */
	@Deprecated
	public void statics(String... string);

	/**
	 * Add configuration folder for static resources
	 * 
	 * @param configKey
	 *            the key in the configuration file
	 * @return ConfigFolder
	 */
	public ConfigFolder ConfigFolder(String configKey);

	/**
	 * Add configuration folder for static resources with web accessible flag
	 * 
	 * @param configKey
	 *            the key in the configuration file
	 * @param webAccess
	 *            indicator to whether those resources under this config folder can be accessed by tomcat or some else
	 * @return
	 */
	public ConfigFolder ConfigFolder(String configKey, boolean webAccess);

	/**
	 * @return ConfigFolder web root path folder
	 */
	public ConfigFolder WebRoot();

}
