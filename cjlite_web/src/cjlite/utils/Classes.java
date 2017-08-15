/**
 * 
 */
package cjlite.utils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author kevin
 * 
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public final class Classes {

	private static final char PACKAGE_SEPARATOR = '.';
	public static final String CLASS_FILE_SUFFIX = ".class";
	public static final String ARRAY_SUFFIX = "[]";
	private static final String INTERNAL_ARRAY_PREFIX = "[L";

	private static final Map primitiveTypeNameMap = new HashMap(16);
	private static final Map<Class<?>, Class<?>> primitiveWrapperTypeMap = new HashMap<Class<?>, Class<?>>(8);

	private static final Map<Class<?>, Class<?>> primitiveWrapperMap = new HashMap<Class<?>, Class<?>>() {

		{
			put(boolean.class, Boolean.class);
			put(char.class, Character.class);
			put(byte.class, Byte.class);
			put(short.class, Short.class);
			put(int.class, Integer.class);
			put(long.class, Long.class);
			put(float.class, Float.class);
			put(double.class, Double.class);
		}
	};

	static {
		primitiveWrapperTypeMap.put(Boolean.class, boolean.class);
		primitiveWrapperTypeMap.put(Byte.class, byte.class);
		primitiveWrapperTypeMap.put(Character.class, char.class);
		primitiveWrapperTypeMap.put(Double.class, double.class);
		primitiveWrapperTypeMap.put(Float.class, float.class);
		primitiveWrapperTypeMap.put(Integer.class, int.class);
		primitiveWrapperTypeMap.put(Long.class, long.class);
		primitiveWrapperTypeMap.put(Short.class, short.class);

		Set primitiveTypeNames = new HashSet(16);
		primitiveTypeNames.addAll(primitiveWrapperTypeMap.values());
		primitiveTypeNames.addAll(Arrays.asList(new Class[] { boolean[].class, byte[].class, char[].class,
				double[].class, float[].class, int[].class, long[].class, short[].class }));
		for (Iterator it = primitiveTypeNames.iterator(); it.hasNext();) {
			Class primitiveClass = (Class) it.next();
			primitiveTypeNameMap.put(primitiveClass.getName(), primitiveClass);
		}
	}

	public static Class<?> resolveClassName(String className, ClassLoader classLoader) {
		try {
			return forName(className, classLoader);
		} catch (ClassNotFoundException ex) {
			IllegalArgumentException iae = new IllegalArgumentException("Cannot find class [" + className + "]");
			iae.initCause(ex);
			throw iae;
		} catch (LinkageError ex) {
			IllegalArgumentException iae = new IllegalArgumentException("Error loading class [" + className
					+ "]: problem with class file or dependent class.");
			iae.initCause(ex);
			throw iae;
		}
	}

	private static Class<?> forName(String name, ClassLoader classLoader) throws ClassNotFoundException {
		Class clazz = resolvePrimitiveClassName(name);
		if (clazz != null) {
			return clazz;
		}

		// "java.lang.String[]" style arrays
		if (name.endsWith(ARRAY_SUFFIX)) {
			String elementClassName = name.substring(0, name.length() - ARRAY_SUFFIX.length());
			Class elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		// "[Ljava.lang.String;" style arrays
		int internalArrayMarker = name.indexOf(INTERNAL_ARRAY_PREFIX);
		if (internalArrayMarker != -1 && name.endsWith(";")) {
			String elementClassName = null;
			if (internalArrayMarker == 0) {
				elementClassName = name.substring(INTERNAL_ARRAY_PREFIX.length(), name.length() - 1);
			} else if (name.startsWith("[")) {
				elementClassName = name.substring(1);
			}
			Class elementClass = forName(elementClassName, classLoader);
			return Array.newInstance(elementClass, 0).getClass();
		}

		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) {
			classLoaderToUse = getDefaultClassLoader();
		}
		return classLoaderToUse.loadClass(name);
	}

	public static Class resolvePrimitiveClassName(String name) {
		Class result = null;
		// Most class names will be quite long, considering that they
		// SHOULD sit in a package, so a length check is worthwhile.
		if (name != null && name.length() <= 8) {
			// Could be a primitive - likely.
			result = (Class) primitiveTypeNameMap.get(name);
		}
		return result;
	}

	public static ClassLoader getDefaultClassLoader() {
		ClassLoader cl = null;
		try {
			cl = Thread.currentThread().getContextClassLoader();
		} catch (Throwable ex) {
			// Cannot access thread context ClassLoader - falling back to system class loader...
		}
		if (cl == null) {
			// No thread context class loader -> use class loader of this class.
			cl = Classes.class.getClassLoader();
		}
		return cl;
	}

	/**
	 * Determine the name of the class file, relative to the containing package: e.g. "String.class"
	 * 
	 * @param clazz
	 *            the class
	 * @return the file name of the ".class" file
	 */
	public static String getClassFileName(Class<?> clazz) {
		String className = clazz.getName();
		int lastDotIndex = className.lastIndexOf(PACKAGE_SEPARATOR);
		return className.substring(lastDotIndex + 1) + CLASS_FILE_SUFFIX;
	}
}
