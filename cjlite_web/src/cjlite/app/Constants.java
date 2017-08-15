package cjlite.app;

public final class Constants {

	public static final class Default {

		/**
		 * Default Encoding
		 */
		public static final String Encoding = "UTF-8";

		/**
		 * Config parameter which should be specify in web filter configuration
		 */
		public static final String ConfigParamsKey = "cjlite_config_file";

		/**
		 * Default configuration file name which locate in class path
		 */
		public static final String DefaultConfigFileInClassPath = "appconfig.properties";

		/**
		 * Class Path Protocol
		 */
		public static final String ClassPathProtocol = "ClassPath://";

	}

	public static final class WebDefault {

		/**
		 * Web Root Path
		 */
		public static final String RootPath = "RootPath";

		/**
		 * Web Root Path
		 */
		public static final String ContextPath = "ContextPath";

		/**
		 * Default path for configuration
		 */
		public static final String ConfigFilePath = "WEB-INF/config/appconfig.properties";

	}

	public static final class Stage {

		public static final String Development = "development";
	}

}
