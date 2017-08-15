package cjlite.plugin;

import com.google.inject.Module;

/**
 * The Plugin of Cjlite web tool
 * 
 * @author kevin
 *
 */
public interface Plugin extends Module{
	/**
	 * Return Plugin Name
	 * 
	 * @return
	 */
	public String getName();
}
