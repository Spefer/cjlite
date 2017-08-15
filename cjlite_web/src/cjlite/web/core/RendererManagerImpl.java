/**
 * 
 */
package cjlite.web.core;

import java.util.HashMap;
import java.util.Map;

import cjlite.utils.Strings;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.View;
import cjlite.web.render.RenderException;
import cjlite.web.render.Renderer;
import cjlite.web.render.RendererManager;

/**
 * @author kevin
 * 
 */
public class RendererManagerImpl implements RendererManager {

	private Map<String, Renderer> rendererMap = new HashMap<String, Renderer>();

	public RendererManagerImpl(Map<String, Renderer> map) {
		this.rendererMap.putAll(map);
	}

	@Override
	public Renderer getRenderer(ModelView modelView) throws RenderException {
		View view = modelView.getView();
		Renderer renderer = rendererMap.get(view.getType());
		if (renderer == null) {
			String msg = Strings
					.fillArgs("Renderer for View'{0}' is not register in system, please register it before using",
							view.getType());
			throw new RenderException(msg);
		}
		return renderer;
	}

}
