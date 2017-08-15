/**
 * 
 */
package cjlite.utils;

import java.io.File;

/**
 * @author kevin
 * 
 */
public final class FilePath {

	private static final char windowsSperatorChar = '\\';
	private static final char linuxSperatorChar = '/';

	private static final String windowsSperator = "\\";
	private static final String linuxSperator = "/";

	public static String join(String path, String... otherPaths) {
		if (otherPaths == null) {
			return path;
		}

		StringBuilder builder = path != null ? new StringBuilder(path) : new StringBuilder();

		for (String eachPath : otherPaths) {
			char lastChar = builder.charAt(builder.length() - 1);
			if (lastChar == windowsSperatorChar || lastChar == linuxSperatorChar) {
				// do nothing
			} else {
				builder.append(File.separator);
			}

			if (eachPath.startsWith(windowsSperator) || eachPath.startsWith(linuxSperator)) {
				builder.append(eachPath, 1, eachPath.length());
			} else {
				builder.append(eachPath);
			}
		}

		return builder.toString();
	}
	
	/**
	 * join given path with "/" and start with "/"
	 * 
	 * @param path
	 * @param otherPaths
	 * @return
	 */
	public static String toUrlPath(String path, String... otherPaths) {
		String newPath = urlJoin(path, otherPaths);

		if (!newPath.startsWith(linuxSperator)) {
			newPath = linuxSperator + newPath;
		}
		return newPath;
	}

	/**
	 * just join given path with "/"
	 * 
	 * @param path
	 * @param otherPaths
	 * @return
	 */
	public static String urlJoin(String path, String... otherPaths) {
		if (otherPaths == null) {
			return path;
		}

		StringBuilder builder = path != null ? new StringBuilder(path) : new StringBuilder();
		for (String eachPath : otherPaths) {
			if(builder.length()==0) {
				builder.append(linuxSperatorChar);
			} else if (builder.charAt(builder.length()-1) == linuxSperatorChar) {
				//do nothing
			}else{
				builder.append(linuxSperatorChar);
			}
			
			if (eachPath.startsWith(windowsSperator) || eachPath.startsWith(linuxSperator)) {
				builder.append(eachPath, 1, eachPath.length());
			} else {
				builder.append(eachPath);
			}
		}

		return builder.toString();
	}

}
