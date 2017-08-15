package cjlite.config;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import cjlite.web.ConfigFolder;

/**
 * Configuration Variables Pool
 * 
 * <pre>
 * In Web Project, There are 2 variables: 
 * 1: RootPath: current app root folder path 
 * 2: contextpath: web app context path
 * </pre>
 * 
 * @author kevin
 * 
 */
public interface Config {

	/**
	 * Get properties value by give key
	 * 
	 * @param key
	 * @return
	 */
	public String getProperties(String key);

	/**
	 * Get properties value by give key, if the value is null, would return defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public String getProperties(String key, String defaultValue);

	/**
	 * get Int value by given key
	 * 
	 * @param key
	 * @return
	 */
	public int getInt(String key);

	/**
	 * get Int value by give key, if not exit, return defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public int getInt(String key, int defaultValue);

	/**
	 * get Long value by given key
	 * 
	 * @param key
	 * @return
	 */
	public long getLong(String key);

	/**
	 * get Long value by give key, if not exit, return defaultValue
	 * 
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public long getLong(String key, long defaultValue);

	public boolean getBool(String key);

	public boolean getBool(String key, boolean defaultValue);

	@Deprecated
	public void addStaticRoot(String... staticPaths);

	@Deprecated
	public List<String> getStaticPaths();

	/**
	 * The Properties in this config
	 * 
	 * @return
	 */
	public Properties getOriginalProperties();

	/**
	 * Rebuild properties by given propsPrefixKey
	 * 
	 * @param string
	 * @return
	 */
	public Properties getPropsPrefixStartWith(String propsPrefixKey);

	/**
	 * Add a configuration folder by key in configuration properties file
	 * 
	 * @param folder
	 */
	public void addConfigFolder(ConfigFolder folder);

	public ConfigFolder getConfigFolder(String configKey);

	public Map<String, ConfigFolder> getConfigFolders();

	public void setLocale(Locale locale);
}
