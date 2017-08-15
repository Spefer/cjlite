/**
 * 
 */
package cjlite.web.handler;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import cjlite.utils.Maps;
import cjlite.web.mvc.RequestContext;

/**
 * Request Context, we can pass this into path mapping method
 * 
 * @author YunYang
 * 
 */
final class DefaultRequestContext implements RequestContext {

	private final HttpServletRequest request;

	private final HttpServletResponse response;

	private final Map<Class<?>, Object> resultMap = Maps.newHashMap();

	private final Map<String, Object> attributionMap = Maps.newHashMap();

	private PathVariables pathVariables;

	public DefaultRequestContext(HttpServletRequest request, HttpServletResponse response, PathVariables pv) {
		this.request = request;
		this.response = response;
		this.pathVariables = pv;
	}

	@Override
	public HttpServletRequest getRequest() {
		return this.request;
	}

	@Override
	public HttpServletResponse getResponse() {
		return this.response;
	}

	@Override
	public HttpSession getSession() {
		return this.request.getSession();
	}

	@Override
	public void setSession(String key, Object value) {
		this.request.getSession().setAttribute(key, value);
	}

	@Override
	public void add(Class<?> resultClass, Object result) {
		resultMap.put(resultClass, result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> T getResult(Class<T> resultClass) {
		return (T) resultMap.get(resultClass);
	}

	@Override
	public void setAttr(String attrName, Object value) {
		attributionMap.put(attrName, value);
	}

	@Override
	public Object getAttr(String attrName) {
		Object result = attributionMap.get(attrName);
		if (result == null) {
			result = this.request.getAttribute(attrName);
		}
		return result;
	}

	@Override
	public Set<String> getAttrNames() {
		Set<String> keys = this.attributionMap.keySet();
		@SuppressWarnings("unchecked")
		Enumeration<String> names = this.request.getAttributeNames();
		while (names.hasMoreElements()) {
			keys.add(names.nextElement());
		}
		return keys;
	}

	@Override
	public String getParameter(String name) {
		return this.request.getParameter(name);
	}

	@Override
	public String[] getParameterValues(String name) {
		return this.request.getParameterValues(name);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return this.request.getParameterNames();
	}

	@Override
	public Map<String, String[]> getParameterMap() {
		return this.request.getParameterMap();
	}

	@Override
	public String getPathVariable(String name) {
		return this.pathVariables.getValue(name);
	}

	@Override
	public Map<String, String> getAllPathVariables() {
		return this.pathVariables.getAllPathVariables();
	}

}
