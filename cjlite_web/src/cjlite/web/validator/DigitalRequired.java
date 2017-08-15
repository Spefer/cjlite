/**
 * 
 */
package cjlite.web.validator;

import cjlite.utils.Strings;

/**
 * @author YunYang
 * @version
 */
public class DigitalRequired implements ValidateCallback {

	private final String failMsg;

	/**
	 * 
	 */
	public DigitalRequired(String failMsg) {
		this.failMsg = failMsg;
	}

	@Override
	public boolean valid(String paramName, String paramValue, ValidateResult result, ValidateContext context) {
		if (paramValue == null) {
			result.add(paramName, failMsg);
			return false;
		}

		if (!Strings.isAllDigital(paramValue)) {
			result.add(paramName, failMsg);
			return false;
		}

		return true;
	}

}
