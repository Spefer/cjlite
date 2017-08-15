package cjlite.web.validator;

public class NotNullRequired implements ValidateCallback {

	private String failMsg;

	public NotNullRequired(String failMsg) {
		this.failMsg = failMsg;
	}

	@Override
	public boolean valid(String paramName, String paramValue, ValidateResult result, ValidateContext context) {
		if (paramValue == null) {
			result.add(paramName, failMsg);
			return false;
		}

		return true;
	}

}
