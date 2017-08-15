/**
 * 
 */
package cjlite.web.mvc;

import java.util.HashMap;
import java.util.Map;

/**
 * @author kevin
 * 
 */
public final class Model {

	private Map<String, Object> dataMap = new HashMap<String, Object>();

	private Model() {
	}

	public static Model New() {
		return new Model();
	}

	public void add(String key, Object value) {
		this.dataMap.put(key, value);
	}

	public Map<String, Object> getModelMap() {
		return dataMap;
	}

	public void putAll(Map<String, ?> modelMap) {
		dataMap.putAll(modelMap);
	}

	public void putAll(Model model) {
		this.dataMap.putAll(model.getModelMap());
	}

}
