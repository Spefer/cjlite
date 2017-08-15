/**
 * 
 */
package cjlite.web.mvc;

import java.util.Map;

import javax.inject.Provider;

import cjlite.app.UncheckedException;
import cjlite.utils.Maps;
import cjlite.utils.Strings;
import cjlite.web.handler.UriPath;

/**
 * @author kevin
 */
public class ControllerDefinition implements Provider<ControllerDefinition> {

	// Map<String, PathMapping>: key-> RequestMethod(GET,POST,DELETE.....)
	private Map<UriPath, Map<String, PathMapping>> staticPathMappingMap = Maps.newHashMap();

	// Map<String, PathMapping>: key-> RequestMethod(GET,POST,DELETE.....)
	private Map<UriPath, Map<String, PathMapping>> paramPathMappingMap = Maps.newHashMap();

	private final Class<?> controllerClass;

	private Object controllerInstance;

	public ControllerDefinition(Class<?> _controllerClass) {
		this.controllerClass = _controllerClass;
	}

	@Override
	public ControllerDefinition get() {
		return this;
	}

	/**
	 * Map<String, PathMapping>: key-> RequestMethod(GET,POST,DELETE.....)
	 * 
	 * @return
	 */
	public Map<UriPath, Map<String, PathMapping>> getStaticPathMappingMap() {
		return staticPathMappingMap;
	}

	/**
	 * Map<String, PathMapping>: key-> RequestMethod(GET,POST,DELETE.....)
	 * 
	 * @return
	 */
	public Map<UriPath, Map<String, PathMapping>> getParamPathMappingMap() {
		return paramPathMappingMap;
	}

	public Object getControllerInstance() {
		return controllerInstance;
	}

	/**
	 * @return
	 */
	public Class<?> getControllerClass() {
		return this.controllerClass;
	}

	public void addPathMapping(PathMapping[] pms) {
		for (PathMapping pm : pms) {

			UriPath up = pm.getUriPath();

			if (up.isStaticPath()) {
				this.addPathMapping(staticPathMappingMap, up, pm);
			} else {
				this.addPathMapping(paramPathMappingMap, up, pm);
			}
		}
	}

	private void addPathMapping(Map<UriPath, Map<String, PathMapping>> mappingMap, UriPath up, PathMapping pm) {
		Map<String, PathMapping> map = mappingMap.get(up);
		if (map == null) {
			map = Maps.newHashMap();
			mappingMap.put(up, map);
		}

		String[] methods = pm.getRequestMethod();
		for (String method : methods) {
			PathMapping existOne = map.get(method);
			if (existOne != null) {
				String msg = Strings.fillArgs("PathMapping conflicted: \n exist one : {0}\nnew one : {1}",
						existOne.toString(), pm.toString());
				throw new UncheckedException(msg);
			} else {
				map.put(method, pm);
			}
		}
	}

	public void updateControllerInstance(Object instance) {
		this.controllerInstance = instance;
	}
	
	
	public String toString(){
		return this.controllerClass.getName();
	}
}
