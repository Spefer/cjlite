package cjlite.app.core;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;

import cjlite.app.AppConfig;
import cjlite.app.CjliteApp;
import cjlite.app.Constants;
import cjlite.utils.FilePath;
import cjlite.utils.Strings;
import cjlite.web.WebConfig;
import cjlite.web.WebModule;
import cjlite.web.core.WebBinderImpl;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Stage;

public final class CjliteAppHelper {

	/**
	 * Used for builder CjliteApp and put this into servletContext
	 * 
	 * @param filterConfig
	 * @param webConfig
	 * @return
	 * @throws IOException
	 */
	public synchronized static CjliteApp build(final FilterConfig filterConfig, WebConfig webConfig)
			throws IOException {

		Properties webProps = loadWebConfigProps(filterConfig);

		ConfigImpl config = new ConfigImpl(webProps);
		//
		WebBinderImpl webBinder = new WebBinderImpl(config);
		webConfig.config(webBinder);
		//
		String stageInfo = config.getProperties("Stage", Stage.DEVELOPMENT.name());
		Stage stage = getConfigStage(stageInfo);
		WebModule module = new WebModule(config);

		Injector injector = Guice.createInjector(stage, webBinder, module);

		String name = filterConfig.getFilterName();
		boolean hasName = name != null && name.length() > 0;

		CjliteAppImpl core = hasName ? new CjliteAppImpl(name, stageInfo, injector, config)
				: new CjliteAppImpl(stageInfo, injector, config);

		// bind Application into Servlet Context
		filterConfig.getServletContext().setAttribute(CjliteApp.class.getName(), core);
		return core;
	}

	/**
	 * Load web configuration file
	 * 
	 * @param filterConfig
	 * @return
	 * @throws IOException
	 */
	private static Properties loadWebConfigProps(FilterConfig filterConfig) throws IOException {
		String configFileName = lookupConfigFile(filterConfig);
		if (configFileName == null) {
			return loadWebConfigDefaultProps(filterConfig);
		} else {
			return loadByConfig(filterConfig, configFileName);
		}
	}

	/**
	 * @param filterConfig
	 * @param configFileName
	 * @return
	 * @throws IOException
	 */
	private static Properties loadByConfig(FilterConfig filterConfig, String configFileName) throws IOException {
		String rootPath = filterConfig.getServletContext().getRealPath("/");
		Properties props = new Properties();
		if (Strings.startWithIgnoreCase(configFileName, Constants.Default.ClassPathProtocol)) {
			// if is classPath
			String fileName = Strings.subStringAfter(configFileName, Constants.Default.ClassPathProtocol);
			InputStream is = CjliteAppHelper.class.getResourceAsStream(fileName);
			if (is == null) {
				is = CjliteAppHelper.class.getClassLoader().getResourceAsStream(fileName);
			}
			props.load(is);
		} else {
			// if is normal file path
			String absConfigFilePath = FilePath.join(rootPath, configFileName);
			FileReader reader = new FileReader(absConfigFilePath);
			props.load(reader);
			
		}
		props.put(Constants.WebDefault.RootPath, rootPath);
		props.put(Constants.WebDefault.ContextPath, filterConfig.getServletContext().getContextPath());
		return props;
	}

	/**
	 * @param filterConfig
	 * @return
	 * @throws IOException
	 */
	private static Properties loadWebConfigDefaultProps(FilterConfig filterConfig) throws IOException {
		Properties props = new Properties();
		StringBuilder builder = new StringBuilder();
		String msg = Strings.fillArgs(
				"There is no configuration[{0}] file exist in your filter parameters, we will try to load be default setting\n",
				Constants.Default.ConfigParamsKey);
		builder.append(msg);
		// load from web default
		String rootPath = filterConfig.getServletContext().getRealPath("/");
		String absConfigFilePath = FilePath.join(rootPath, Constants.WebDefault.ConfigFilePath);
		FileReader reader = null;
		try {
			reader = new FileReader(absConfigFilePath);
			props.load(reader);
		} catch (FileNotFoundException e) {
			msg = Strings.fillArgs("Default configuration file[{0}] is not exist in your folder\n",
					Constants.WebDefault.ConfigFilePath);
			builder.append(msg);
			// load from classPath default
			InputStream is = CjliteAppHelper.class.getResourceAsStream(Constants.Default.DefaultConfigFileInClassPath);
			if (is == null) {
				is = CjliteAppHelper.class.getClassLoader()
						.getResourceAsStream(Constants.Default.DefaultConfigFileInClassPath);
			}

			if (is == null) {
				msg = Strings.fillArgs(
						"Default configuration file[ClassPath://{0}] is not exist in your folder\n!! we need a configuration file to initial Cjlite instance",
						Constants.Default.DefaultConfigFileInClassPath);
				builder.append(msg);
				throw new IOException(builder.toString());
			} else {
				props.load(is);
			}
		}
		props.put(Constants.WebDefault.RootPath, rootPath);
		props.put(Constants.WebDefault.ContextPath, filterConfig.getServletContext().getContextPath());
		return props;
	}

	public synchronized static CjliteApp build(String configFileName, AppConfig appConfig) throws IOException {
		// Properties data
		Properties props = loadConfigProperties(CjliteAppHelper.class, configFileName);
		// config with Properties
		ConfigImpl config = new ConfigImpl(props);

		String stage_str = config.getProperties("Stage", Stage.DEVELOPMENT.name());
		Stage stage = getConfigStage(stage_str);

		AppBinderImpl appBinderImpl = new AppBinderImpl(config);
		appConfig.config(appBinderImpl);
		AppModule module = new AppModule(config);
		Injector injector = Guice.createInjector(stage, appBinderImpl, module);
		CjliteAppImpl core = new CjliteAppImpl(stage_str, injector, config);
		return core;
	}

	private static Stage getConfigStage(String stage_str) {
		if (Stage.DEVELOPMENT.name().equalsIgnoreCase(stage_str)) {
			return Stage.DEVELOPMENT;
		} else {
			return Stage.PRODUCTION;
		}
	}

	private static Properties loadConfigProperties(Class<?> loaderClass, String configFileName) throws IOException {
		InputStream is = loaderClass.getResourceAsStream(configFileName);
		if (is == null) {
			is = loaderClass.getClassLoader().getResourceAsStream(configFileName);
		}
		Properties prop = new Properties();
		prop.load(is);
		return prop;
	}

	/**
	 * load properties from given file name, if not exist, try to load from classPath
	 * 
	 * @param absConfigFilePath
	 * @return
	 * @throws IOException
	 */
	private static Properties loadConfigProperties(String absConfigFilePath, String configFileName) throws IOException {
		Properties props = new Properties();
		FileReader reader = null;
		try {
			reader = new FileReader(absConfigFilePath);
			props.load(reader);
		} catch (FileNotFoundException fnfe) {
			InputStream is = CjliteAppHelper.class.getResourceAsStream(configFileName);
			if (is == null) {
				is = CjliteAppHelper.class.getResourceAsStream(Constants.Default.DefaultConfigFileInClassPath);
			}

			if (is == null) {
				String msg = Strings.fillArgs(
						"We can not find your configuration file by following locations:\n{0}\nClasspath:{1}\nClasspath:{2}",
						absConfigFilePath, configFileName, Constants.Default.DefaultConfigFileInClassPath);
				throw new IOException(msg);
			}

			props.load(is);
		}

		return props;
	}

	private static String lookupConfigFile(FilterConfig filterConfig) {
		String file = filterConfig.getInitParameter(Constants.Default.ConfigParamsKey);
		if (file != null) {
			file = file.trim();
		}
		if (file.length() == 0) {
			return null;
		}
		return file;
	}

	/**
	 * Get CjliteApp from given ServletContext
	 * 
	 * @param context
	 * @return
	 */
	public static CjliteApp getCjliteApp(ServletContext context) {
		return (CjliteApp) context.getAttribute(CjliteApp.class.getName());
	}
}
