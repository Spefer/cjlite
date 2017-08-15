package cjlite.web.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {

	/**
	 * The value may indicate a suggestion for a logical component name,
	 * 
	 * @return the suggested component name, if any
	 */
	String value() default "";

}