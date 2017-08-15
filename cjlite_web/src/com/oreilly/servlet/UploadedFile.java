package com.oreilly.servlet;

import java.io.File;

// A class to hold information about an uploaded file.
// No Set Method for this bean class
//
public class UploadedFile {

	private final String dir;
	private final String filename;
	private final String original;
	private final String type;
	private final String saveDirectory;

	public UploadedFile(String dir, String filename, String original, String type, String saveDir) {
		this.dir = dir;
		this.filename = filename;
		this.original = original;
		this.type = type;
		this.saveDirectory = saveDir;
	}

	public String getContentType() {
		return type;
	}

	public String getDir() {
		return dir;
	}

	public String getFilesystemName() {
		return filename;
	}

	public String getOriginalFileName() {
		return original;
	}

	public File getFile() {
		if (dir == null || filename == null) {
			return null;
		} else {
			return new File(dir + File.separator + filename);
		}
	}

	public String getSaveDirectory() {
		return saveDirectory;
	}


}