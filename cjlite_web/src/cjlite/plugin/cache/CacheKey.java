/**
 * 
 */
package cjlite.plugin.cache;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author YunYang
 * @version Nov 12, 2015 6:03:03 PM
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CacheKey {
	/**
	 * The sequence of this cache key value in cache name
	 * 
	 * @return
	 */
	int seq() default 0;
}
