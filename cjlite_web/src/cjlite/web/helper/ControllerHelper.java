/**
 * 
 */
package cjlite.web.helper;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import cjlite.log.Logger;
import cjlite.utils.Maps;
import cjlite.utils.ResourceUtils;
import cjlite.utils.Strings;
import cjlite.web.annotations.Controller;
import cjlite.web.annotations.Intercept;
import cjlite.web.annotations.Path;
import cjlite.web.annotations.RequestMethod;
import cjlite.web.core.InterceptorCollector;
import cjlite.web.core.InterceptorCollectorType;
import cjlite.web.interceptor.ControllerInterceptor;
import cjlite.web.interceptor.InterceptorException;
import cjlite.web.mvc.ControllerDefinition;
import cjlite.web.mvc.MethodParameter;
import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.PathMapping;

/**
 * @author kevin
 * 
 */
public final class ControllerHelper {

	private static Logger logger = Logger.thisClass();

	/**
	 * Load the controller class into list by given package
	 * 
	 * @param _package
	 *            given package
	 * @param clazzList
	 *            given class List
	 * 
	 * @see cjlite.web.annotation.Controller
	 */
	public static void loadControllerClass(Package _package, List<Class<?>> clazzList) {
		String pkgname = _package.getName();
		String relPath = pkgname.replace('.', '/');
		Enumeration<URL> urls = null;
		try {
			urls = ControllerHelper.class.getClassLoader().getResources(relPath);
			if (urls == null) {
				return;
			}
			while (urls.hasMoreElements()) {
				URL resourceUrl = urls.nextElement();
				if (ResourceUtils.isJarURL(resourceUrl)) {
					handleJarResource(relPath, resourceUrl, clazzList);
				} else {
					File directory = new File(resourceUrl.getFile());
					handleFileResource(relPath, directory, clazzList);
				}
			}
		} catch (IOException e) {
			logger.error("ClassNotFoundException loading for package path: {0}", relPath);
		}
	}

	/**
	 * @param relPath
	 * @param resourceUrl
	 * @param clazzList
	 */
	private static void handleJarResource(String relPath, URL resourceUrl, List<Class<?>> clazzList) {
		JarFile jarFile = null;
		String jarFileUrl = null;
		String classPrefixPath = relPath;
		boolean newJarFile = false;
		try {
			URLConnection con = resourceUrl.openConnection();
			if (con instanceof JarURLConnection) {
				JarURLConnection jarCon = (JarURLConnection) con;
				jarCon.setUseCaches(false);
				jarFile = jarCon.getJarFile();
				jarFileUrl = jarCon.getJarFileURL().toExternalForm();
			} else {
				String urlFile = resourceUrl.getFile();
				int separatorIndex = urlFile.indexOf(ResourceUtils.JAR_URL_SEPARATOR);
				if (separatorIndex != -1) {
					jarFileUrl = urlFile.substring(0, separatorIndex);
					jarFile = ResourceUtils.getJarFile(jarFileUrl);
				} else {
					jarFile = new JarFile(urlFile);
					jarFileUrl = urlFile;
				}
				newJarFile = true;
			}

//			logger.trace("Looking for matching resources in jar file [{0}]", jarFileUrl);

			if (!classPrefixPath.endsWith("/")) {
				classPrefixPath = classPrefixPath + "/";
			}

			for (Enumeration<?> entries = jarFile.entries(); entries.hasMoreElements();) {
				JarEntry entry = (JarEntry) entries.nextElement();
				String entryPath = entry.getName();
				if (entryPath.startsWith(classPrefixPath)) {
					// String relativePath =
					// entryPath.substring(rootEntryPath.length());
					String className = entryPath.replace('/', '.').replace('\\', '.');
					className = getClassName(className);
					Class<?> clazz = newClassByName(className);
					if (clazz == null)
						continue;
					if (isControllerType(clazz)) {
						clazzList.add(clazz);
					}
				}
			}

		} catch (IOException e) {
			logger.error("error when Looking for matching resources in jar file [{0}]", e, resourceUrl.getPath());
		} finally {
			if (newJarFile) {
				try {
					jarFile.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private static void handleFileResource(String relPath, File directory, List<Class<?>> clazzList) {
		if (directory.exists() && directory.isDirectory()) {
			File[] nfiles = directory.listFiles();
			for (int i = 0; i < nfiles.length; i++) {

				if (nfiles[i].isDirectory()) {
					handleFileResource(relPath, nfiles[i], clazzList);
					continue;
				}

				String className = getFileClassName(relPath, nfiles[i]);
				Class<?> clazz = newClassByName(className);
				if (clazz == null)
					continue;
				if (isControllerType(clazz)) {
					clazzList.add(clazz);
				}
			}
		}
	}

	private static String getFileClassName(String relatedpath, File file) {
		if (!relatedpath.endsWith("/")) {
			relatedpath = relatedpath + "/";
		}

		String path = file.getAbsolutePath();
		path = path.replace('\\', '/');
		int index = path.lastIndexOf(relatedpath);

		if (index >= 0) {
			String className = path.substring(index);
			className = className.replace('/', '.');
			className = getClassName(className);
			return className;
		}

		return "";
	}


	public static boolean isJarURL(URL url) {
		return ResourceUtils.isJarURL(url);
	}

	private static boolean isControllerType(Class<?> type) {
		if (type.getAnnotation(Controller.class) != null) {
			return true;
		}
		return false;
	}

	private static String getClassName(String classString) {
		if (classString.endsWith(".class")) {
			return classString.substring(0, classString.length() - 6);
		}
		return "";
	}

	private static Class<?> newClassByName(String className) {
		if (className.trim().length() == 0)
			return null;
		try {
			Class<?> clazz = Class.forName(className.trim());
			return clazz;
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException loading " + className);
		}

		return null;
	}

	private static Map<Class<?>, MethodParamParser> mppMap = Maps.newHashMap();

	public static ControllerDefinition parse(InterceptorCollector parent, Class<?> controllerClass) {
		// Class Interceptors
		InterceptorCollector classCollector = new InterceptorCollector(InterceptorCollectorType.Type,
				controllerClass.getName(), parent);
		Class<? extends ControllerInterceptor>[] typeInterceptors = getTypeInterceptors(controllerClass);
		classCollector.add(typeInterceptors);

		ControllerDefinition cd = new ControllerDefinition(controllerClass);
		try {
			Path typeLevelPath = controllerClass.getAnnotation(Path.class);
			Method[] methods = controllerClass.getMethods();

			for (Method method : methods) {
				if (!isValidated(method)) {
					continue;
				}
				// Path annotated to method
				Path methodLevelPath = method.getAnnotation(Path.class);
				// Method Intercepter
				InterceptorCollector methodCollector = new InterceptorCollector(InterceptorCollectorType.Method,
						method.getName(), classCollector);
				Class<? extends ControllerInterceptor>[] methodInterceptors = getMethodInterceptors(method);
				methodCollector.add(methodInterceptors);

				PathMapping[] pms = null;
				MethodParamParser mpp = mppMap.get(method.getDeclaringClass());
				if (mpp == null) {
					mpp = new MethodParamParser(method.getDeclaringClass());
					mppMap.put(method.getDeclaringClass(), mpp);
				}

				if (methodLevelPath != null) {
					if (typeLevelPath != null) {
						pms = getPathMappings(cd, typeLevelPath, methodLevelPath, method, mpp, methodCollector);
					} else {
						pms = getPathMappings(cd, methodLevelPath, method, mpp, methodCollector);
					}
				} else {
					if (method.getName().equals("index") && typeLevelPath != null) {
						pms = getPathMappings(cd, typeLevelPath, method, mpp, methodCollector);
					}
				}
				//
				if (pms != null) {
					cd.addPathMapping(pms);
				}
			}
		} catch (Exception e) {
			logger.error("error on parse Controller Class '{0}'", e, controllerClass.getName());
			return null;
		}

		return cd;
	}

	private static PathMapping[] getPathMappings(ControllerDefinition cd, Path typeLevelPath, Path methodLevelPath,
			Method method, MethodParamParser mpp, InterceptorCollector methodCollector) {

		boolean hasTypeLevelPath = typeLevelPath.value() != null && typeLevelPath.value().length > 0;
		boolean hasMethodLevelPath = methodLevelPath.value() != null && methodLevelPath.value().length > 0;

		if (hasTypeLevelPath) {
			int typeLevelLength = typeLevelPath.value().length;
			if (hasMethodLevelPath) {
				int methodLevelLength = methodLevelPath.value().length;

				PathMapping[] pms = new PathMapping[typeLevelLength * methodLevelLength];
				RequestMethod[] requestMethods = methodLevelPath.method();
				MethodParameter[] mps = mpp.getMethodParameters(method);
				int i = 0;
				for (String typePathValue : typeLevelPath.value()) {
					for (String methodPathValue : methodLevelPath.value()) {
						pms[i++] = new PathMapping(cd, concat(typePathValue, methodPathValue), requestMethods, method,
								mps, methodCollector);
					}
				}
				return pms;
			} else {
				PathMapping[] pms = new PathMapping[typeLevelLength];
				RequestMethod[] requestMethods = methodLevelPath.method();
				MethodParameter[] mps = mpp.getMethodParameters(method);
				int i = 0;
				for (String typePathValue : typeLevelPath.value()) {
					pms[i++] = new PathMapping(cd, typePathValue, requestMethods, method, mps, methodCollector);
				}
				return pms;
			}
		} else {
			return getPathMappings(cd, methodLevelPath, method, mpp, methodCollector);
		}
	}

	private static String concat(String typePathValue, String methodPathValue) {
		if (methodPathValue.trim().length() == 0) {
			return typePathValue;
		}

		if (typePathValue.endsWith("/")) {
			if (methodPathValue.startsWith("/")) {
				return typePathValue + (methodPathValue.substring(1));
			} else {
				return typePathValue + methodPathValue;
			}
		} else {
			if (methodPathValue.startsWith("/")) {
				return typePathValue + methodPathValue;
			} else {
				return typePathValue + "/" + methodPathValue;
			}
		}
	}

	private static boolean isValidated(Method method) {
		if (Object.class.equals(method.getDeclaringClass())) {
			return false;
		}

		if (Modifier.isAbstract(method.getModifiers())) {
			return false;
		}

		if (ModelView.class.isAssignableFrom(method.getReturnType())) {
			return true;
		}
		return false;
	}

	private static PathMapping[] getPathMappings(ControllerDefinition cd, Path path, Method method,
			MethodParamParser mpp, InterceptorCollector methodCollector) {
		if (path.value() != null && path.value().length > 0) {
			int length = path.value().length;

			PathMapping[] pms = new PathMapping[length];

			RequestMethod[] requestMethods = path.method();

			MethodParameter[] mps = mpp.getMethodParameters(method);

			int i = 0;
			for (String pathValue : path.value()) {
				pms[i++] = new PathMapping(cd, pathValue, requestMethods, method, mps, methodCollector);
			}
			return pms;
		}
		return null;
	}

	public static ModelView invoke(Object controllerInstance, PathMapping mapping, Object[] invokeParams)
			throws InterceptorException {
		Method method = mapping.getMappingMethod();
		try {
			return (ModelView) method.invoke(controllerInstance, invokeParams);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String msg = Strings.fillArgs("error in invoke method '{0}' on class '{1}', because : {2}",
					method.getName(), method.getDeclaringClass().getName(), e.getMessage());
			throw new InterceptorException(msg, e);
		}
	}

	public static ModelView invoke(Object controllerInstance, PathMapping mapping) throws InterceptorException {
		Method method = mapping.getMappingMethod();
		try {
			return (ModelView) method.invoke(controllerInstance);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String msg = Strings.fillArgs("error in invoke method '{0}' on class '{1}', because : {2}",
					method.getName(), method.getDeclaringClass().getName(), e.getMessage());
			throw new InterceptorException(msg, e);
		}
	}

	public static Class<? extends ControllerInterceptor>[] parsePackageInterceptors(Package cpackage) {
		Intercept interceptor = cpackage.getAnnotation(Intercept.class);
		if (interceptor != null) {
			return interceptor.value();
		}
		return null;
	}

	private static Class<? extends ControllerInterceptor>[] getTypeInterceptors(Class<?> controllerClass) {
		Intercept interceptor = controllerClass.getAnnotation(Intercept.class);
		if (interceptor != null) {
			return interceptor.value();
		}
		return null;
	}

	private static Class<? extends ControllerInterceptor>[] getMethodInterceptors(Method method) {
		Intercept interceptor = method.getAnnotation(Intercept.class);
		if (interceptor != null) {
			return interceptor.value();
		}
		return null;
	}

}
