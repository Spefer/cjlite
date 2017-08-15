/**
 * 
 */
package cjlite.web;

import javax.inject.Provider;
import javax.servlet.ServletContext;

import cjlite.web.filter.CjliteFilter;

/**
 * @author kevin
 *
 */
public class ServletContextProvider implements Provider<ServletContext> {

	@Override
	public ServletContext get() {
		return CjliteFilter.getServletContext();
	}

}
