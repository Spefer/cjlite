/**
 * 
 */
package cjlite.web.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import cjlite.web.render.ResponseViewRenderer;
import cjlite.web.render.TextRenderer;
import cjlite.web.render.Renderer;
import cjlite.web.render.RendererManager;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

/**
 * @author YunYang
 * 
 */
public class RendererManagerProvider implements Provider<RendererManager> {

	private static final TypeLiteral<Renderer> renderType = TypeLiteral.get(Renderer.class);

	@Inject
	private Injector injector;

	@Override
	public RendererManager get() {
		// Renderer name and renderer instance
		Map<String, Renderer> rendererMap = new HashMap<String, Renderer>();
		this.buildRendererMap(rendererMap);
		return new RendererManagerImpl(rendererMap);
	}

	private void buildRendererMap(Map<String, Renderer> rendererMap) {
		this.registerSystemRenderer(rendererMap);
		List<Binding<Renderer>> list = injector.findBindingsByType(renderType);

		for (Binding<Renderer> binding : list) {
			Renderer rnder = binding.getProvider().get();
			rendererMap.put(rnder.getView(), rnder);
			rnder.initial();
		}

	}

	/**
	 * @param rendererMap
	 * 
	 */
	private void registerSystemRenderer(Map<String, Renderer> rendererMap) {
		Renderer renderer = new ResponseViewRenderer();
		rendererMap.put(renderer.getView(), renderer);
		renderer.initial();

		renderer = new TextRenderer();
		rendererMap.put(renderer.getView(), renderer);
		renderer.initial();
	}
}
