/**
 * 
 */
package cjlite.web.helper;

import java.util.List;

import cjlite.web.WebConfig;

/**
 * @author YunYang
 * @Date 2017-03-27
 * @version
 */
public class WebConfigHelper {

	/**
	 * @param controllerPackage
	 * @param list
	 */
	public static void loadConfigClass(Package _package, List<Class<?>> clazzList) {
		ClazzHelper.loadClassesBy(_package, clazzList, c -> {
			for (Class<?> cz : c.getInterfaces()) {
				if (WebConfig.class.equals(cz)) {
					return true;
				}
			}
			return false;
		});
	}

}
