/**
 * 
 */
package cjlite.plugin.cache;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import cjlite.error.ErrorMsg;
import cjlite.log.Logger;
import cjlite.utils.Lists;
import cjlite.utils.Strings;

/**
 * @author YunYang
 * @version Oct 29, 2015 1:53:31 PM
 */
public class CacheInterceptor implements MethodInterceptor {

	private static final Logger logger = Logger.thisClass();

	public static final String INTERCEPTOR_CACHE_NAME = "InterceptorCache";

	private CacheManager cacheMgr;

	private ICache interceptorCache;

	@Inject
	private void setCacheManager(CacheManager cacheMgr) {
		this.cacheMgr = cacheMgr;
		this.interceptorCache = this.cacheMgr.getCache(INTERCEPTOR_CACHE_NAME);
		if (this.interceptorCache == null) {
			this.interceptorCache = this.cacheMgr.newCache(INTERCEPTOR_CACHE_NAME);
		}
	}

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Cache cache = invocation.getMethod().getAnnotation(Cache.class);
		if (cache == null) {
			return invocation.proceed();
		}

		if (RefreshType.New == cache.type()) {
			return this.handleGet(cache, invocation);
		} else {
			return this.handleLoadThenRefresh(cache, invocation);
		}
	}

	private Object handleLoadThenRefresh(Cache cache, MethodInvocation invocation) throws Throwable {
		List<CacheEvent> events = this.newEvents(cache, invocation);
		this.cacheMgr.fireEvent(events);
		return invocation.proceed();
	}

	private List<CacheEvent> newEvents(Cache cache, MethodInvocation invocation) {
		StringBuilder sourcePositionbuilder = new StringBuilder();
		sourcePositionbuilder.append(invocation.getMethod().getDeclaringClass().getName());
		sourcePositionbuilder.append(".");
		sourcePositionbuilder.append(invocation.getMethod().getName());
		sourcePositionbuilder.append("()");

		List<CacheEvent> events = Lists.newArrayList();
		String paramsKey = this.paramsKey(invocation);
		for (String cacheKey : cache.value()) {
			String key = cacheKey + paramsKey;
			events.add(new CacheEvent(key, sourcePositionbuilder.toString(), cache.type(), invocation, cache.match()));
		}
		return events;
	}

	private boolean validate(MethodInvocation invocation, ErrorMsg error) {
		if (invocation.getArguments().length == 0) {
			return true;
		}

		Class<?>[] params = invocation.getMethod().getParameterTypes();
		if (params.length == 1) {
			if (ErrorMsg.class.equals(params[0])) {
				return true;
			}
		}

		String msg = Strings.fillArgs(
				"The method[{0}.{1}()] which has Cache Annotation only accept 1 parameter and it should be 'cjlite.error.ErrorMsg.class' ",
				invocation.getMethod().getDeclaringClass().getName(), invocation.getMethod().getName());
		error.add(msg);
		return false;
	}

	String error01 = "Only 1 value is permitted for cache Mehtod[{0}.{1}()] when cache.type() is RefreshType.Get, "
			+ "please reduce it to 1, \\n currently you have: {2}";

	private Object handleGet(Cache cache, MethodInvocation invocation) throws Throwable {
		int len = cache.value().length;
		if (len > 1) {
			String msg = Strings.fillArgs(error01, invocation.getMethod().getDeclaringClass().getName(),
					invocation.getMethod().getName(), Arrays.toString(cache.value()));
			throw new Exception(msg);
		}

		String name = cache.value()[0] + this.paramsKey(invocation);
		CacheElement element = interceptorCache.getOrCreate(name, invocation.getMethod().toString());
		Object result = element.getCachedValue();
		if (result == null) {
			result = invocation.proceed();
			element.putCacheValue(result);
		}
		return result;
	}

	/**
	 * return value format: valuevalue
	 * 
	 * @param invocation
	 * @return
	 */
	private String paramsKey(MethodInvocation invocation) {
		Annotation[][] pass = invocation.getMethod().getParameterAnnotations();
		int length = pass.length;
		StringBuilder builder = new StringBuilder();
		boolean hasCacheKey = false;

		for (int i = 0; i < length; i++) {
			Annotation[] pas = pass[i];
			if (hasCacheKey(pas)) {
				hasCacheKey = true;
				Object paramValue = invocation.getArguments()[i];
				builder.append(paramValue);
			}
		}

		if (!hasCacheKey) {
			return "";
		}

		return builder.toString();
	}

	private boolean hasCacheKey(Annotation[] pas) {
		for (Annotation pa : pas) {
			if (CacheKey.class.equals(pa.annotationType())) {
				return true;
			}
		}

		return false;
	}
}
