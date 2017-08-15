/**
 * 
 */
package cjlite.web.mvc;

import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @author YunYang
 * @version
 */
public interface RequestContext {

	HttpServletRequest getRequest();

	HttpServletResponse getResponse();

	HttpSession getSession();

	void setSession(String key, Object value);

	void add(Class<?> resultClass, Object result);

	<T> T getResult(Class<T> resultClass);

	void setAttr(String attrName, Object value);

	Object getAttr(String attrName);

	Set<String> getAttrNames();

	String getParameter(String name);

	String[] getParameterValues(String name);

	Enumeration<String> getParameterNames();

	Map<String, String[]> getParameterMap();

	String getPathVariable(String name);

	Map<String, String> getAllPathVariables();
}