/**
 * 
 */
package cjlite.web.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cjlite.web.interceptor.ControllerInterceptor;

/**
 * <pre>
 * Do form validation:<br>
 * you need to create a Class which should be extends from <b><code>AbstractFormValidator</code></b>,<br>
 * And pass <b><code>RequestContext</code></b> as parameters into that controller method, <br>
 * And next you can get <b><code>ValidateResult</code></b> from <b><code>RequestContext</code></b>.<br>
 * Like: <b><code>ValidateResult result= RequestContext.getResult(ValidateResult.class)</code></b>
 * 
 * Do File upload:<br>
 * you need to create a Class which should be extends from <b><code>AbstractFileUpload</code></b>,<br>
 * And pass <b><code>RequestContext</code></b> as parameters into that controller method, <br>
 * And next you can get <b><code>UploadResult</code></b> from <b><code>RequestContext</code></b>.<br>
 * Like: <b><code>UploadResult result= RequestContext.getResult(UploadResult.class)</code></b>
 * </pre>
 * 
 * @author kevin
 * 
 */
@Target({ ElementType.METHOD, ElementType.TYPE, ElementType.PACKAGE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Intercept {
	Class<? extends ControllerInterceptor>[] value() default {};
}
