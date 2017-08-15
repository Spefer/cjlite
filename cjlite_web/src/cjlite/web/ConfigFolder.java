/**
 * 
 */
package cjlite.web;

import java.util.List;
import java.util.Map;

import cjlite.config.Config;
import cjlite.utils.Lists;
import cjlite.utils.Strings;

/**
 * @author ming
 *
 */
public final class ConfigFolder {

	private final String configFolderkey;
	private List<StaticFolderName> fodlerPathList = Lists.newArrayList();
	private final boolean webRootFolder;
	private final boolean webAccess;
	private final Config config;

	private static final String existFolderError = "The static folder name '{0}' already configurated at {1}, Please user different name at {3}";

	public ConfigFolder(Config config, String configKey, boolean webRoot, boolean webAccess) {
		this.config = config;
		this.configFolderkey = configKey;
		this.webRootFolder = webRoot;
		this.webAccess = webAccess;
	}

	public void statics(String folderName, String... folderNames) {
		if (folderName == null) {
			throw new IllegalArgumentException("folderName in parameter can not be null");
		}

		StackTraceElement[] source = new Exception().getStackTrace();
		ConfigSourcePosition sourcePosition = new ConfigSourcePosition(source[1]);

		StaticFolderName existOne = isExist(folderName);
		if (existOne != null) {
			String msg = Strings.fillArgs(existFolderError, folderName, existOne.getConfigSourcePosition().toString(),
					sourcePosition.toString());
			throw new RuntimeException(msg);
		}

		fodlerPathList.add(new StaticFolderName(folderName, sourcePosition));

		if (folderNames != null) {
			existOne = null;
			for (String folder : folderNames) {
				existOne = isExist(folder);
				if (existOne != null) {
					String msg = Strings.fillArgs(existFolderError, folder, existOne.getConfigSourcePosition()
							.toString(), sourcePosition.toString());
					throw new RuntimeException(msg);
				}
				fodlerPathList.add(new StaticFolderName(folder, sourcePosition));
			}
		}
	}

	private StaticFolderName isExist(String folderName) {
		Map<String, ConfigFolder> cfgFolderMap = config.getConfigFolders();

		for (ConfigFolder cfolder : cfgFolderMap.values()) {
			List<StaticFolderName> listF = cfolder.getStaticFolder();
			for (StaticFolderName staticFolder : listF) {
				if (staticFolder.getFolderName().equals(folderName)) {
					return staticFolder;
				}
			}
		}

		return null;
	}
	
	
	

	@Override
	public int hashCode() {
		final int prime = 127;
		int result = 1;
		result = prime * result + ((config == null) ? 0 : config.hashCode());
		result = prime * result + ((configFolderkey == null) ? 0 : configFolderkey.hashCode());
		result = prime * result + (webAccess ? 1231 : 1237);
		result = prime * result + (webRootFolder ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ConfigFolder other = (ConfigFolder) obj;
		if (config == null) {
			if (other.config != null)
				return false;
		} else if (!config.equals(other.config))
			return false;
		if (configFolderkey == null) {
			if (other.configFolderkey != null)
				return false;
		} else if (!configFolderkey.equals(other.configFolderkey))
			return false;
		if (webAccess != other.webAccess)
			return false;
		if (webRootFolder != other.webRootFolder)
			return false;
		return true;
	}

	/**
	 * Indicate this Configfolder if is a web root folder
	 * 
	 * @return
	 */
	public boolean isWebRootFolder() {
		return this.webRootFolder;
	}

	public String getConfigKey() {
		return this.configFolderkey;
	}

	/**
	 * @return folders user setted in config
	 */
	public List<StaticFolderName> getStaticFolder() {
		return this.fodlerPathList;
	}

	public boolean contain(String staticFolder) {
		for (StaticFolderName folder : fodlerPathList) {
			if (folder.getFolderName().equals(staticFolder)) {
				return true;
			}
		}
		return false;
	}

	public boolean isWebAccess() {
		return this.webAccess;
	}

}
