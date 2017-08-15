package cjlite.web.handler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import cjlite.utils.Maps;

/**
 * @author ming
 */
public class PathVariables {

	private Map<String, String> variables = new HashMap<String, String>();

	public PathVariables() {
	}

	public boolean hasVariables() {
		return !this.variables.isEmpty();
	}

	public void addVar(String name, String value) {
		variables.put(name, value);
	}

	public String getValue(String paramName) {
		return variables.get(paramName);
	}

	/**
	 * @return
	 */
	Map<String, String> getAllPathVariables() {
		Map<String, String> clone = Maps.newHashMap();
		this.variables.forEach((k, v) -> {
			clone.put(k, v);
		});
		return clone;
	}

}
