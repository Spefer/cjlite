/**
 * 
 */
package cjlite.web.validator;

import java.util.List;
import java.util.Map;

import cjlite.utils.Lists;
import cjlite.utils.Maps;

/**
 * @author YunYang
 * @version Jul 2, 2015 10:33:12 AM
 */
public class ValidateResultImpl implements ValidateResult {

	private Map<String, List<String>> result = Maps.newHashMap();
	private Map<String, String> formValue = Maps.newHashMap();

	public ValidateResultImpl() {
	}

	@Override
	public boolean isPass() {
		return result.size() == 0;
	}

	@Override
	public void addValue(String paramName, String value) {
		formValue.put(paramName, value);
	}

	@Override
	public void add(String paramName, String failMsg) {
		List<String> rl = result.get(paramName);
		if (rl == null) {
			rl = Lists.newArrayList();
			result.put(paramName, rl);
		}
		rl.add(failMsg);
	}

	@Override
	public Map<String, List<String>> getFailResult() {
		return this.result;
	}

	@Override
	public String getFieldValue(String key) {
		return formValue.get(key);
	}

}
