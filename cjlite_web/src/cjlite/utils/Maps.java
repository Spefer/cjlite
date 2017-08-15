/**
 * 
 */
package cjlite.utils;

import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * @author kevin
 * 
 */
public class Maps {

	public static <K, V> Map<K, V> newHashMap() {
		return new HashMap<K, V>();
	}


	public static <K, V> Map<K, V> newIdentityHashMap() {
		return new IdentityHashMap<K, V>();
	}

}
