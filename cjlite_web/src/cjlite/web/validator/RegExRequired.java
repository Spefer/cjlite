package cjlite.web.validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExRequired implements ValidateCallback {

	private final String regEx;
	private final String failMsg;
	private final Pattern pattern;

	public RegExRequired(String regEx, String failMsg) {
		this.regEx = regEx;
		this.failMsg = failMsg;
		this.pattern = Pattern.compile(this.regEx);
	}

	@Override
	public boolean valid(String paramName, String paramValue, ValidateResult result, ValidateContext context) {
		if (paramValue == null) {
			result.add(paramName, failMsg);
			return false;
		}

		Matcher m = pattern.matcher(paramValue);
		boolean mresult = m.matches();
		if (!mresult) {
			result.add(paramName, failMsg);
			return false;
		}
		return true;
	}

}
