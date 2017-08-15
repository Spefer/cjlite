package cjlite.web.filter;

import cjlite.web.WebBinder;
import cjlite.web.WebConfig;

public final class DefaultFilter extends CjliteFilter {

	@Override
	protected WebConfig getWebConfig() {
		return new WebConfig(){

			@Override
			public void config(WebBinder binder) {
				
			}

		};
	}

}
