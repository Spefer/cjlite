/**
 * 
 */
package cjlite.plugin.renderer.json;

import javax.inject.Provider;

import cjlite.plugin.RendererPlugin;
import cjlite.web.mvc.Views;
import cjlite.web.render.Renderer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.internal.UniqueAnnotations;

/**
 * @author kevin
 * 
 */
public abstract class JsonPlugin extends RendererPlugin {

	@Override
	public String getName() {
		return Views.Json;
	}

	@Override
	public void configure(Binder binder) {
		Key<Renderer> key = Key.get(Renderer.class, UniqueAnnotations.create());
		binder.bind(key).to(getRendererClass()).in(Scopes.SINGLETON);
		binder.bind(JsonConfig.class).toProvider(this.getJsonConfigProvider()).in(Scopes.SINGLETON);
	}

	public abstract Class<? extends Provider<JsonConfig>> getJsonConfigProvider();

	@Override
	public Class<? extends Renderer> getRendererClass() {
		return JsonRenderer.class;
	}

}
