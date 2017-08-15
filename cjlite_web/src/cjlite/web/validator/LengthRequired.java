/**
 * 
 */
package cjlite.web.validator;

/**
 * @author YunYang
 * @version Jul 2, 2015 11:37:32 AM
 */
public class LengthRequired implements ValidateCallback {

	private final int min;
	private final int max;
	private final String failMsg;

	/**
	 * @param min
	 * @param max
	 * @param failMsg
	 */
	public LengthRequired(int min, int max, String failMsg) {
		this.min = min;
		this.max = max;
		this.failMsg = failMsg;
	}

	@Override
	public boolean valid(String paramName, String paramValue, ValidateResult result, ValidateContext context) {
		if (paramValue == null) {
			result.add(paramName, failMsg);
			return false;
		}
		int length = paramValue.trim().length();

		if (length < min || length > max) {
			result.add(paramName, failMsg);
			return false;
		}
		return true;
	}

}
