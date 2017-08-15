/**
 * 
 */
package cjlite.web.helper;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Stream;

import cjlite.log.Logger;
import cjlite.utils.ResourceUtils;

/**
 * @author YunYang
 * @Date 2017-03-27
 * @version
 */
public class ClazzHelper {

	private static Logger logger = Logger.thisClass();

	/**
	 * @param _package
	 * @param clazzList
	 * @param clazzMatcher
	 */
	public static void loadClassesBy(Package _package, List<Class<?>> clazzList, ClazzMatcher<Class<?>> clazzMatcher) {
		String pkgname = _package.getName();
		String relPath = pkgname.replace('.', '/');
		Enumeration<URL> urls = null;
		try {
			urls = ClazzHelper.class.getClassLoader().getResources(relPath);
			if (urls == null) {
				return;
			}
			while (urls.hasMoreElements()) {
				URL resourceUrl = urls.nextElement();
				if (ResourceUtils.isJarURL(resourceUrl)) {
					handleJarResource(relPath, resourceUrl, clazzList, clazzMatcher);
				} else {
					File directory = new File(resourceUrl.getFile());
					handleFileResource(relPath, directory, clazzList, clazzMatcher);
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
	 * @param clazzMatcher
	 */
	private static void handleJarResource(String relPath, URL resourceUrl, List<Class<?>> clazzList,
			ClazzMatcher<Class<?>> clazzMatcher) {
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

			final String cpp = classPrefixPath;

			Stream<JarEntry> jes = jarFile.stream().filter(je -> je.getName().startsWith(cpp));
			jes.forEach(je -> {
				String className = je.getName().replace('/', '.').replace('\\', '.');
				className = getClassName(className);
				Class<?> clazz = newClassByName(className);
				if (clazz != null && clazzMatcher.isMatch(clazz)) {
					clazzList.add(clazz);
				}
			});

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

	/**
	 * @param relPath
	 * @param directory
	 * @param clazzList
	 * @param clazzMatcher
	 */
	private static void handleFileResource(String relPath, File directory, List<Class<?>> clazzList,
			ClazzMatcher<Class<?>> clazzMatcher) {
		if (directory.exists() && directory.isDirectory()) {
			File[] nfiles = directory.listFiles();
			for (int i = 0; i < nfiles.length; i++) {
				if (nfiles[i].isDirectory()) {
					handleFileResource(relPath, nfiles[i], clazzList, clazzMatcher);
					continue;
				}

				String className = getFileClassName(relPath, nfiles[i]);
				Class<?> clazz = newClassByName(className);
				if (clazz == null)
					continue;
				if (clazzMatcher.isMatch(clazz)) {
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

}
