/**
 * 
 */
package cjlite.plugin;

import cjlite.web.render.Renderer;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.internal.UniqueAnnotations;

/**
 * @author kevin
 * 
 */
public abstract class RendererPlugin implements Plugin {

	public abstract Class<? extends Renderer> getRendererClass();

	@Override
	public void configure(Binder binder) {
		Key<Renderer> key = Key.get(Renderer.class, UniqueAnnotations.create());
		binder.bind(key).to(getRendererClass()).in(Scopes.SINGLETON);
	}
}
