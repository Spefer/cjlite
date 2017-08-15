/**
 * 
 */
package cjlite.web.helper;

/**
 * @author YunYang
 * @Date 2017-03-27
 * @version
 */
@FunctionalInterface
interface ClazzMatcher<T> {

	boolean isMatch(T t);
}
