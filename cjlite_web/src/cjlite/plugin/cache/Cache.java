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
 * value: cache name array. normally, it's length would be 1 if you want to cache something by current annotated with this Cache annotation.
 * otherwise, you can use multiple value if you want to refresh some cache by that values
 * 
 * type: 
 * 
 * 
 * 
 * @author YunYang
 * @version Oct 29, 2015 1:51:28 PM
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Cache {
	/**
	 * @return the part of cache element name or full cache name which would be stored in cache within cache manager
	 */
	String[] value();

	/**
	 * <pre>
	 * specify the Cache type:
	 * New : normal process, just load the data from cache, if null load from original method;
	 * 
	 * RefreshEvent: which need to be fired to refresh cache element
	 * LoadThenRefresh : If this cached method accessed by any other method, it would perform load option when event is LoadThenRefresh;
	 * </pre>
	 * 
	 * @return
	 */
	RefreshType type() default RefreshType.New;

	/**
	 * <pre>
	 * 3 matchers:
	 * 
	 * 1, FullName: means when we fire a cache refresh request, it would look up cache element name by full name matched.
	 * 2, StartWith: means when we fire a cache refresh request, it would look up cache element name by name start with matched.
	 * 3. EndWith: means when we fire a cache refresh request, it would look up cache element name by name end with matched.
	 * 
	 * </pre>
	 * 
	 * @return
	 */
	NameMatcher match() default NameMatcher.FullName;

	/**
	 * tell us it would include parameter value
	 * 
	 * @return
	 * @see cjlite.plugin.cache.CacheKey
	 */
	boolean withParamValue() default false;
}
