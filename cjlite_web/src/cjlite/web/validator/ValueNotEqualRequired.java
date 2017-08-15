package cjlite.web.validator;

public class ValueNotEqualRequired implements ValidateCallback {

	private final Object targetValue;
	private final String failMsg;

	public ValueNotEqualRequired(Object result, String failMsg) {
		this.targetValue = result;
		this.failMsg = failMsg;
	}

	@Override
	public boolean valid(String paramName, String paramValue, ValidateResult result, ValidateContext context) {
		if (paramValue == null) {
			result.add(paramName, failMsg);
			return false;
		}
		String v = paramValue.trim();

		if (v.equals(String.valueOf(targetValue))) {
			result.add(paramName, failMsg);
			return false;
		}
		return true;
	}

}
