package cjlite.plugin.renderer.json;

import javax.inject.Provider;


public class DefaultJsonPlugin extends JsonPlugin {

	@Override
	public Class<? extends Provider<JsonConfig>> getJsonConfigProvider() {
		return JsonConfigProvider.class;
	}

}
