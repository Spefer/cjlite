package cjlite.web.handler;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.Lists;
import cjlite.utils.Maps;
import cjlite.utils.Strings;
import cjlite.utils.TypeConverter;
import cjlite.web.helper.UrlHelper;
import cjlite.web.interceptor.ControllerInterceptor;
import cjlite.web.interceptor.InterceptorChain;
import cjlite.web.mvc.ControllerDefinition;
import cjlite.web.mvc.MethodParameter;
import cjlite.web.mvc.Model;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.PathMapping;
import cjlite.web.mvc.PathRedirectView;
import cjlite.web.mvc.RequestContext;
import cjlite.web.render.Renderer;
import cjlite.web.render.RendererManager;

import com.google.inject.Binding;
import com.google.inject.ConfigurationException;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

public class ControllerHandler implements Handler {

	private static final Logger logger = Logger.thisClass();

	@Inject
	private Config config;

	@Inject
	private Injector injector;

	@Inject
	private RendererManager rendererMgr;

	// Map<String, PathMapping>: key->is Request Method,as GET,POST
	private Map<UriPath, Map<String, PathMapping>> fixPathMapping = Maps.newHashMap();

	// Map<String, PathMapping>: key->is Request Method,as GET,POST
	private Map<UriPath, Map<String, PathMapping>> paramsPathMapping = Maps.newHashMap();

	private final TypeConverter typeConvertor = new TypeConverter();

	private boolean useInterceptorCached = false;

	// Map<String, List<ControllerInterceptor>>: key->is Request Method,as GET,POST
	private Map<UriPath, Map<String, List<ControllerInterceptor>>> interceptorCacheList = Maps.newHashMap();

	//
	private static final TypeLiteral<ControllerDefinition> controllerType = TypeLiteral.get(ControllerDefinition.class);

	/*
	 * initial Controller Handler
	 */
	public void initial() {
		useInterceptorCached = this.config.getBool("interceptor.usecached", false);
		List<Binding<ControllerDefinition>> cdlist = this.injector.findBindingsByType(controllerType);

		for (Binding<ControllerDefinition> binding : cdlist) {
			ControllerDefinition cd = binding.getProvider().get();
			fixPathMapping.putAll(cd.getStaticPathMappingMap());
			paramsPathMapping.putAll(cd.getParamPathMappingMap());
		}

	}

	@Override
	public void handle(HttpServletRequest request, HttpServletResponse response, HandlerChain handlerChain) {
		String requestPath = UrlHelper.getRequestPath(request);
		String requestMethod = request.getMethod();
		requestMethod = requestMethod.toUpperCase();
		ModelView mv = null;
		try {
			Model model = Model.New();
			mv = handleRequest(requestPath, requestMethod, model, request, response);
		} catch (HandleException he) {
			this.toRuntimeException(he, request, response);
			return;
		}
		
		// If no matched PathMapping exist, it would return this directly
		if (mv == null) {
			logger.warn("modelview is null because no matched PathMapping exist for url: {0}[{1}]", requestPath,
					requestMethod);
			return;
		}

		this.mergeSystemModel(mv);

		this.renderModelView(mv, request, response);
	}

	/**
	 * process request by request path and request method then return a ModelView
	 * 
	 * @param requestPath
	 * @param requestMethod
	 * @param model
	 * @param request
	 * @param response
	 * @return
	 * @throws HandleException
	 */
	private ModelView handleRequest(String requestPath, String requestMethod, Model model, HttpServletRequest request,
			HttpServletResponse response) throws HandleException {
		MatchResult matchResult = this.findMatchMapping(requestPath, requestMethod);
		if (matchResult == null) {
			logger.warn("Path Mapping for URI'{0}'is not found in register table", requestPath);
			return null;
		}

		ModelView result = this.processRequest(matchResult, request, response);

		//till now, it should have a ModelView, after controller and intercepter
		//if it is a null, we assign a empty ModelView to it.
		if (result == null) {
			result = ModelView.Empty();
			return result;
		}
		
		result.getModel().putAll(model);


		if (PathRedirectView.class.isInstance(result.getView())) {
			PathRedirectView redirect = (PathRedirectView) result.getView();
			String path = redirect.getRedirectControllerPath();
			String method = redirect.getMethod().name();
			logger.trace("path redirect to url: {0}[{1}]", path, method);
			return handleRequest(path, method, result.getModel(), request, response);
		}

		return result;
	}

	/**
	 * process matchresult and return a ModelView
	 * 
	 * @param matchResult
	 * @param request
	 * @param response
	 * @return
	 * @throws HandleException
	 */
	private ModelView processRequest(MatchResult matchResult, HttpServletRequest request, HttpServletResponse response)
			throws HandleException {
		PathMapping mapping = matchResult.getPathMapping();
		ModelView result = null;
		Object controllerInstance = this.getControllerInstance(matchResult);
		//
		int paramLength = mapping.getMethodParametersLength();
		List<ControllerInterceptor> cilist = getControllerInterceptorList(mapping, matchResult.getRequestMethod());

		if (paramLength == 0) {
			result = new InterceptorChain(request, response, cilist, controllerInstance, mapping, new Object[] {},
					matchResult.getPathVariables()).invoke();
		} else {
			Object[] paramValues = this.buildMethodParams(mapping, matchResult.getPathVariables(), request, response);
			result = new InterceptorChain(request, response, cilist, controllerInstance, mapping, paramValues,
					matchResult.getPathVariables()).invoke();
		}

		return result;
	}

	/**
	 * get controller instance from Injector by given MatchResult
	 * 
	 * @param matchResult
	 * @return
	 * @throws HandleException
	 * 
	 * @see cjlite.web.handler.MatchResult
	 */
	private Object getControllerInstance(MatchResult matchResult) throws HandleException {
		PathMapping mapping = matchResult.getPathMapping();
		ControllerDefinition controllerDef = mapping.getControllerDefinition();
		Object controllerInstance = this.getControllerInstance(controllerDef);
		return controllerInstance;
	}

	private void renderModelView(ModelView mv, HttpServletRequest request, HttpServletResponse response) {
		try {
			//
			Renderer renderer = this.rendererMgr.getRenderer(mv);
			renderer.render(mv, request, response);
		} catch (Exception e) {
			logger.error("Error on process request:{0}", e, e.getMessage());
			this.toRuntimeException(e, request, response);
		}
	}

	/**
	 * @param exception
	 * @param request
	 * @param response
	 * @throws Exception
	 */
	private void toRuntimeException(Exception exception, HttpServletRequest request, HttpServletResponse response) {
		throw new RuntimeException(exception.getMessage(), exception.getCause());
	}

	/**
	 * Get Controller Interceptor Instance List whatever <code>useInterceptorCached</code> true or false
	 * 
	 * @param mapping
	 * @param string
	 * @return
	 */
	private List<ControllerInterceptor> getControllerInterceptorList(PathMapping mapping, String method) {
		// use cache
		if (!useInterceptorCached) {
			List<ControllerInterceptor> list = Lists.newArrayList();
			this.buildControllerInterceptorList(list, mapping);
			return list;
		}

		Map<String, List<ControllerInterceptor>> cacheMap = interceptorCacheList.get(mapping.getUriPath());
		if (cacheMap == null) {
			cacheMap = Maps.newHashMap();
			interceptorCacheList.put(mapping.getUriPath(), cacheMap);
		}

		List<ControllerInterceptor> list = cacheMap.get(method);

		if (list == null) {
			list = Lists.newArrayList();
			this.buildControllerInterceptorList(list, mapping);
			cacheMap.put(method, list);
		}

		return list;
	}

	/**
	 * Build Controller Interceptor instance List from mapping Interceptor Class List
	 * 
	 * @param list
	 * @param mapping
	 */
	private void buildControllerInterceptorList(List<ControllerInterceptor> list, PathMapping mapping) {
		List<Class<? extends ControllerInterceptor>> classList = mapping.getInterceptorClassList();
		for (Class<? extends ControllerInterceptor> interClass : classList) {
			ControllerInterceptor interceptor = this.injector.getInstance(interClass);
			list.add(interceptor);
		}

	}

	private void mergeSystemModel(ModelView modelView) {

	}

	private Object[] buildMethodParams(PathMapping mapping, PathVariables pathVariables, HttpServletRequest request,
			HttpServletResponse response) {
		MethodParameter[] mps = mapping.getMethodParameters();
		Object[] paraValues = new Object[mps.length];
		for (int i = 0; i < mps.length; i++) {
			paraValues[i] = this.buildParaValue(mps[i], pathVariables, request, response);
		}

		return paraValues;
	}

	private Object buildParaValue(MethodParameter methodParameter, PathVariables pv, HttpServletRequest request,
			HttpServletResponse response) {
		Class<?> paramType = methodParameter.getParamType();
		if (paramType.isPrimitive() || String.class.equals(paramType)) {
			String paramName = methodParameter.getParamName();
			String value = pv.getValue(paramName);
			if (value == null) {
				return null;
			} else {
				if (typeConvertor.canConvert(paramType)) {
					return typeConvertor.convert(paramType, value);
				}
				return value;
			}
		} else {

			if (ServletRequest.class.isAssignableFrom(paramType)) {
				return request;
			}
			if (ServletResponse.class.isAssignableFrom(paramType)) {
				return response;
			}
			if (HttpSession.class.isAssignableFrom(paramType)) {
				return request.getSession();
			}
			if (RequestContext.class.isAssignableFrom(paramType)) {
				RequestContext context = new DefaultRequestContext(request, response, pv);
				return context;
			}
		}
		return null;
	}

	/**
	 * Get contoller instance from Injector
	 * 
	 * @param controllerDef
	 * @return
	 */
	private Object getControllerInstance(ControllerDefinition controllerDef) throws HandleException {
		Object controller = controllerDef.getControllerInstance();
		if (controller == null) {
			try {
				controller = injector.getInstance(controllerDef.getControllerClass());
			} catch (ConfigurationException ce) {
				String errorMsg = Strings.fillArgs("Error in instance of contoller class'{0}', because '{1}'",
						controllerDef.getControllerClass().getName(), ce.getMessage());
				throw new HandleException(errorMsg, ce);
			}
			controllerDef.updateControllerInstance(controller);
		}
		return controller;
	}

	private MatchResult findMatchMapping(String requestPath, String requestMethod) {
		UriPath path = new UriPath(requestPath, false);

		MatchResult result = null;
		String msg = Strings.fillArgs("\nRequest Path:{0}[{1}]", requestPath, requestMethod);
		StringBuilder builder = new StringBuilder();
		builder.append(msg);
		Map<String, PathMapping> mappingMap = this.fixPathMapping.get(path);
		if (mappingMap != null) {
			builder.append("\nExist Path:").append(requestPath);
			PathMapping mapping = mappingMap.get(requestMethod);
			if (mapping != null) {
				builder.append("[").append(requestMethod).append("]");
				result = new MatchResult(mapping, true, requestPath, requestMethod);
			} else {
				builder.append("[").append(mappingMap.keySet().toString()).append("]");
			}
		}

		if (result == null) {
			PathVariables pv = new PathVariables();
			for (UriPath up : paramsPathMapping.keySet()) {
				if (up.match(requestPath, pv)) {
					mappingMap = paramsPathMapping.get(up);
					builder.append("\nExist Path:").append(up.path());
					PathMapping mapping = mappingMap.get(requestMethod);
					if (mapping != null) {
						builder.append("[").append(requestMethod).append("]");
						result = new MatchResult(mapping, false, requestPath, requestMethod, pv);
						builder.append("[from: ")
								.append(mapping.getControllerDefinition().getControllerClass().getName()).append(".")
								.append(mapping.getMappingMethod().getName()).append("()]");
					} else {
						builder.append("[").append(mappingMap.keySet().toString()).append("]");
					}

				}
			}
		}

		logger.debug(builder.toString());

		return result;
	}

	@Override
	public String getName() {
		return "ControllerHandler";
	}

}
