/**
 * 
 */
package cjlite.plugin.renderer.freemarker;

import cjlite.plugin.RendererPlugin;
import cjlite.web.mvc.Views;
import cjlite.web.render.Renderer;

/**
 * @author kevin
 * 
 */
public class FreeMarkerPlugin extends RendererPlugin {

	@Override
	public String getName() {
		return Views.FreeMarker;
	}

	@Override
	public Class<? extends Renderer> getRendererClass() {
		return FreeMarkerRenderer.class;
	}

}
