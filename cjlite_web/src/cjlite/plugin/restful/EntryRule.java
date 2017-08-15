/**
 * 
 */
package cjlite.plugin.restful;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(METHOD)
/**
 * @author YunYang
 * @version
 */
public @interface EntryRule {

	/**
	 * @return EntryParam array
	 */
	EntryParam[] value() default {};

	/**
	 * Return value should be RestResult
	 * 
	 * @return EntryParam array
	 */
	String[] returns() default {};

	/**
	 * @return Entry Description
	 */
	String desc() default "";
}
