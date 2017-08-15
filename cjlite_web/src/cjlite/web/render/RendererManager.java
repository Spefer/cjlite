package cjlite.web.render;

import cjlite.web.mvc.ModelView;

/**
 * Renderer Manager to manage the render in IOC container
 * 
 * @author kevin
 * 
 */
public interface RendererManager {

	Renderer getRenderer(ModelView modelView) throws RenderException;

}
