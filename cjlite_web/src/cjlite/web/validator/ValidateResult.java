/**
 * 
 */
package cjlite.web.validator;

import java.util.List;
import java.util.Map;

/**
 * @author YunYang
 * @version Jul 2, 2015 10:32:45 AM
 */
public interface ValidateResult {

	/**
	 * return pass or not for this validation
	 * 
	 * @return
	 */
	public boolean isPass();

	/**
	 * If the value for form'd field is valid, then put it in
	 * 
	 * @param paramName
	 * @param value
	 */
	public void addValue(String paramName, String value);

	/**
	 * If the value for form'd field is invalid, then put it in with fail message
	 * 
	 * @param paramName
	 * @param failMsg
	 */
	public void add(String paramName, String failMsg);

	/**
	 * @return fail result
	 */
	public Map<String, List<String>> getFailResult();

	/**
	 * @param string
	 * @return form field value
	 */
	public String getFieldValue(String string);

}
