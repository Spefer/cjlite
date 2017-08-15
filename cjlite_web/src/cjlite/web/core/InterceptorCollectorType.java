/**
 * 
 */
package cjlite.web.core;

/**
 * @author YunYang
 * @version Aug 3, 2015 3:39:38 PM
 */
public enum InterceptorCollectorType {
	/**
	 * This is configurated in WebConfig type
	 */
	Global,

	/**
	 * annotated in Package-info.type
	 */
	Package,

	/**
	 * annotated in type name
	 */
	Type,

	/**
	 * annotated in method name
	 */
	Method;
}
