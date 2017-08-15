/**
 * 
 */
package cjlite.web.render;

import com.google.inject.Provider;

/**
 * 
 * 
 * @author kevin
 *
 */
public abstract class AbstractRenderer implements Renderer, Provider<Renderer> {

	@Override
	public Renderer get() {
		this.initial();
		return this;
	}

}
