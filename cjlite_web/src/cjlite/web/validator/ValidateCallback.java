package cjlite.web.validator;

public interface ValidateCallback {
	/**
	 * Validate paramValue map to paramName from Http Request and add validate result into ValidateResult
	 * 
	 * @param paramName
	 * @param paramValue
	 * @param result
	 * @param context 
	 */
	public boolean valid(String paramName, String paramValue, ValidateResult result, ValidateContext context);
}
