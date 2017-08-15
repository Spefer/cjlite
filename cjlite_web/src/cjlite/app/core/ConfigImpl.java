package cjlite.app.core;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import cjlite.config.Config;
import cjlite.i18n.I18n;
import cjlite.log.Logger;
import cjlite.utils.Lists;
import cjlite.utils.Maps;
import cjlite.web.ConfigFolder;

class ConfigImpl implements Config {

	private static final Logger logger = Logger.thisClass();

	private final Properties configProps;

	private List<String> staticPath = Lists.newArrayList();

	private Map<String, ConfigFolder> configFolderMap = Maps.newHashMap();

	private Locale locale;

	ConfigImpl(Properties props) {
		this.configProps = props;
	}

	@Override
	public String getProperties(String key) {
		return configProps.getProperty(key);
	}

	@Override
	public String getProperties(String key, String defaultValue) {
		return configProps.getProperty(key, defaultValue);
	}

	@Override
	public int getInt(String key) {
		String value = getProperties(key);
		return Integer.parseInt(value);
	}

	@Override
	public int getInt(String key, int defaultValue) {
		try {
			return getInt(key);
		} catch (Exception e) {
			logger.warn("value[{0}] of the key[{1}] is not legal number, return default value[{2}]", getProperties(key),
					key, defaultValue);
			return defaultValue;
		}
	}

	@Override
	public boolean getBool(String key) {
		String value = this.getProperties(key);
		return Boolean.parseBoolean(value);
	}

	@Override
	public boolean getBool(String key, boolean defaultValue) {
		String value = this.getProperties(key);
		if (value == null) {
			return defaultValue;
		}
		return Boolean.parseBoolean(value);
	}

	@Override
	public void addStaticRoot(String... staticPaths) {
		for (String path : staticPaths) {
			staticPath.add(path);
		}
	}

	public List<String> getStaticPaths() {
		return this.staticPath;
	}

	@Override
	public Properties getOriginalProperties() {
		Properties pops = new Properties();
		pops.putAll(this.configProps);
		return pops;
	}

	@Override
	public Properties getPropsPrefixStartWith(String propsPrefixKey) {
		Properties pops = new Properties();
		for (String key : this.configProps.stringPropertyNames()) {
			if (key.startsWith(propsPrefixKey)) {
				String value = this.configProps.getProperty(key);
				pops.put(key, value);
			}
		}
		return pops;
	}

	@Override
	public void addConfigFolder(ConfigFolder folder) {
		this.configFolderMap.put(folder.getConfigKey(), folder);
	}

	@Override
	public ConfigFolder getConfigFolder(String configKey) {
		return this.configFolderMap.get(configKey);
	}

	@Override
	public Map<String, ConfigFolder> getConfigFolders() {
		return this.configFolderMap;
	}

	@Override
	public long getLong(String key) {
		String value = getProperties(key);
		return Long.valueOf(value);
	}

	@Override
	public long getLong(String key, long defaultValue) {
		try {
			return getLong(key);
		} catch (Exception e) {
			logger.warn("value[{0}] of the key[{1}] is not legal number, return default value[{2}]", getProperties(key),
					key, defaultValue);
			return defaultValue;
		}
	}

	@Override
	public void setLocale(Locale locale) {
		this.locale = locale;
		I18n.build(locale);
	}

}
