/**
 * 
 */
package cjlite.web.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <pre>
 * For a special situation:
 * 
 * If have a <b>Path</b> annotation in Type level, and no any method annotated with <b>Path</b>,
 * 
 * We would look up <b>index</b> method as default mapping for this controller type.
 * 
 * which means, any request for this Path in Type level, would route to this <b>index</b> method
 * 
 * </pre>
 * 
 * @author ming
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Path {

	/**
	 * @return path url
	 */
	String[] value() default {};

	/**
	 * Default is GET
	 * 
	 * @return RequestMethod
	 */
	RequestMethod[] method() default { RequestMethod.GET };

	/**
	 * Can be used as URl 'alt' text or menuItem text
	 * 
	 * @return
	 */
	String text() default "";
}
