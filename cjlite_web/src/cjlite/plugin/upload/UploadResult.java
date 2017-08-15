/**
 * 
 */
package cjlite.plugin.upload;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import com.oreilly.servlet.UploadedFile;

/**
 * @author YunYang
 * @version Jul 29, 2015 7:03:00 PM
 */
public interface UploadResult {

	boolean isSuccessful();

	List<String> getErrors();

	Hashtable<Object, Vector<String>> getParameters();
	
	Vector<String> getParams(String name);
	
	String getParam(String name);

	Hashtable<String, UploadedFile> getUploadedFiles();

	UploadedFile getFile(String fileName);
}
