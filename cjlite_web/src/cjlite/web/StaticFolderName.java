/**
 * 
 */
package cjlite.web;

/**
 * @author YunYang Jan 22, 2016
 */
public final class StaticFolderName {

	private final ConfigSourcePosition configSourcePosition;
	private final String folderName;

	public StaticFolderName(String folderName, ConfigSourcePosition configSourcePosition) {
		this.folderName = folderName;
		this.configSourcePosition = configSourcePosition;
	}

	public String getFolderName() {
		return folderName;
	}

	public ConfigSourcePosition getConfigSourcePosition() {
		return configSourcePosition;
	}

	@Override
	public String toString() {
		return "StaticFolderName [folderName=" + folderName + ", configSourcePosition=" + configSourcePosition + "]";
	}
	
	

}
