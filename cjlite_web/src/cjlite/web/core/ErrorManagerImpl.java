/**
 * 
 */
package cjlite.web.core;

import java.util.Map;

import cjlite.error.ErrorManager;
import cjlite.utils.Maps;

/**
 * @author YunYang
 * @version Sep 18, 2015 3:14:40 PM
 */
public class ErrorManagerImpl implements ErrorManager {

	private Map<String, Exception> exceptionMap = Maps.newHashMap();

	@Override
	public void add(String code, Exception e) {
		exceptionMap.put(code, e);
	}

	@Override
	public Exception getException(String code) {
		return exceptionMap.get(code);
	}

}
