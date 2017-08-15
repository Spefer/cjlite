/**
 * 
 */
package cjlite.web;

import javax.servlet.ServletContext;

import cjlite.app.core.AppModule;
import cjlite.config.Config;
import cjlite.error.ErrorManager;
import cjlite.web.core.ErrorManagerImpl;
import cjlite.web.core.HandlerManagerProvider;
import cjlite.web.core.NotFoundHandlerImpl;
import cjlite.web.core.RendererManagerProvider;
import cjlite.web.core.StaticResourceManagerProvider;
import cjlite.web.handler.HandlerManager;
import cjlite.web.render.RedirectRenderer;
import cjlite.web.render.Renderer;
import cjlite.web.render.RendererManager;
import cjlite.web.statics.StaticResourceManager;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.internal.UniqueAnnotations;

/**
 * Web System Module
 * 
 * @author kevin
 * 
 */
public class WebModule extends AppModule {

	public WebModule(Config config) {
		super(config);
	}

	@Override
	public void webConfigure(Binder binder) {
		binder.bind(HandlerManager.class).toProvider(HandlerManagerProvider.class).in(Scopes.SINGLETON);
		binder.bind(RendererManager.class).toProvider(RendererManagerProvider.class).in(Scopes.SINGLETON);
		binder.bind(ServletContext.class).toProvider(ServletContextProvider.class).in(Scopes.SINGLETON);
		binder.bind(StaticResourceManager.class).toProvider(StaticResourceManagerProvider.class).in(Scopes.SINGLETON);
		binder.bind(NotFoundHandler.class).to(NotFoundHandlerImpl.class).in(Scopes.SINGLETON);
		binder.bind(ErrorManager.class).to(ErrorManagerImpl.class).in(Scopes.SINGLETON);
		this.bindInternalRenderer(binder);
	}

	private void bindInternalRenderer(Binder binder) {
		Key<Renderer> key = Key.get(Renderer.class, UniqueAnnotations.create());
		binder.bind(key).to(RedirectRenderer.class).in(Scopes.SINGLETON);
	}

}
