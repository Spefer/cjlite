/**
 * 
 */
package cjlite.web.filter;

import java.io.IOException;
import java.lang.ref.WeakReference;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cjlite.app.CjliteApp;
import cjlite.app.Constants;
import cjlite.app.core.CjliteAppHelper;
import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.NanoTimer;
import cjlite.web.NotFoundHandler;
import cjlite.web.WebConfig;
import cjlite.web.handler.HandleResult;
import cjlite.web.handler.HandlerManager;
import cjlite.web.statics.StaticResourceManager;

/**
 * @author kevin
 * 
 */
public abstract class CjliteFilter implements Filter {

	private static Logger logger = Logger.thisClass();

	static volatile WeakReference<ServletContext> servletContext = new WeakReference<ServletContext>(null);

	private static CjliteApp app;

	private String encoding = Constants.Default.Encoding;

	private HandlerManager handlerMgr;

	private StaticResourceManager staticMgr;

	private NotFoundHandler notFoundHandler;

	private Config config;

	/*
	 * Initial the CjliteApp
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		NanoTimer timer = new NanoTimer();
		timer.start();
		logger.debug("start to initial Cjlite");
		try {
			servletContext = new WeakReference<ServletContext>(filterConfig.getServletContext());
			app = CjliteAppHelper.build(filterConfig, this.getWebConfig());
		} catch (IOException e) {
			logger.error("CjliteApp initial fail because:", e);
			return;
		}
		handlerMgr = app.getInstance(HandlerManager.class);
		staticMgr = app.getInstance(StaticResourceManager.class);
		this.config = app.getInstance(Config.class);
		notFoundHandler = app.getInstance(NotFoundHandler.class);
		logger.debug("[{0}]startup cost:{1}ms", app.getName(), timer.stop());
	}

	/**
	 * WebConfig
	 * 
	 * @return
	 */
	protected abstract WebConfig getWebConfig();

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		NanoTimer timer = new NanoTimer();
		timer.start();
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) resp;
		//
		//
		HandleResult result = null;
		try {
			// 1. lookup static resources
			result = staticMgr.lookup(request, response);
			if (result != null && !result.isProcessed()) {
				chain.doFilter(request, response);
				// logger.debug("process static resource cost:{0}ms", timer.stop());
				return;
			}
			// 2. handler resources
			if (result == null) {
				request.setCharacterEncoding(encoding);
				response.setCharacterEncoding(encoding);
				result = handlerMgr.handle(request, response);
				// logger.debug("process handler cost:{0}ms", timer.stop());
			}
		} catch (Throwable t) {
			logger.error("error on handle request", t);
		}

		if (result != null && !result.isProcessed()) {
			// Handler NOT Found
			notFoundHandler.handle(request, response);
		}
	}

	@Override
	public void destroy() {
		app.destroy();
	}

	/**
	 * Servlet context
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContext.get();
	}

}
