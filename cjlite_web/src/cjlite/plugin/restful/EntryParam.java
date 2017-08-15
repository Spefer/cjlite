/**
 * 
 */
package cjlite.plugin.restful;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target({ ANNOTATION_TYPE })
/**
 * @author YunYang
 * @version
 */
public @interface EntryParam {

	/**
	 * Parameter Name
	 * 
	 * @return
	 */
	String name();

	/**
	 * Indicate this parameter is required or optional
	 * 
	 * @return
	 */
	PrReqType req() default PrReqType.Explicit;

}
