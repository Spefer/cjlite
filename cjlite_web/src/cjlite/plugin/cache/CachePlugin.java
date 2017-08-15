package cjlite.plugin.cache;

import com.google.inject.Binder;
import com.google.inject.matcher.Matchers;

import cjlite.plugin.Plugin;

public abstract class CachePlugin implements Plugin {

	@Override
	public void configure(Binder binder) {
		this.configCacheMgr(binder);
		CacheInterceptor cache = new CacheInterceptor();
		binder.bindInterceptor(Matchers.any(), Matchers.annotatedWith(Cache.class), cache);
		binder.requestInjection(cache);

	}

	protected abstract void configCacheMgr(Binder binder);

	@Override
	public String getName() {
		return "Cache Plugin";
	}

}
