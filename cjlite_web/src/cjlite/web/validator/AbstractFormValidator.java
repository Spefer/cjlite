package cjlite.web.validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import cjlite.utils.Lists;
import cjlite.utils.Maps;
import cjlite.utils.Strings;
import cjlite.web.interceptor.ControllerInterceptor;
import cjlite.web.interceptor.ControllerInvocation;
import cjlite.web.interceptor.InterceptorChain;

public abstract class AbstractFormValidator implements FormValidator, ControllerInterceptor {

	protected Map<String, List<ValidateCallback>> callbackMap = Maps.newHashMap();

	@Inject
	public AbstractFormValidator() {
		this.initial();
		this.addValidCondition(callbackMap);
	}

	protected abstract void initial();

	@Override
	public Object intercept(ControllerInvocation invocation) throws Exception {

		ValidateResult result = new ValidateResultImpl();
		ValidateContext context = new ValidateContext(invocation);
		this.validate(context, result);

		if (InterceptorChain.class.isInstance(invocation)) {
			InterceptorChain chain = (InterceptorChain) invocation;
			chain.reWorkMethodParam(ValidateResult.class, result);
		}

		return invocation.invoke();
	}

	/**
	 * Parameter's value length should be >=min and <=max, otherwise return failMsg
	 * 
	 * @param paramName
	 *            form field name
	 * @param min
	 * @param max
	 * @param failMsg
	 */
	protected void addLengthRequired(String paramName, int min, int max, String failMsg) {
		failMsg = Strings.fillArgs(failMsg, min, max);
		this.addValidateCallback(paramName, new LengthRequired(min, max, failMsg));
	}

	protected void addDigitalRequired(String paramName, String failMsg) {
		this.addValidateCallback(paramName, new DigitalRequired(failMsg));
	}

	protected void addRegExRequired(String paramName, String regEx, String failMsg) {
		this.addValidateCallback(paramName, new RegExRequired(regEx, failMsg));
	}

	protected void addValueNotEqualRequired(String paramName, Object result, String failMsg) {
		this.addValidateCallback(paramName, new ValueNotEqualRequired(result, failMsg));
	}

	protected void addNotNullRequired(String paramName, String failMsg) {
		this.addValidateCallback(paramName, new NotNullRequired(failMsg));
	}

	protected void addValidateCallback(String paramName, ValidateCallback callback) {
		List<ValidateCallback> callbackList = callbackMap.get(paramName);
		if (callbackList == null) {
			callbackList = Lists.newArrayList();
			callbackMap.put(paramName, callbackList);
		}

		callbackList.add(callback);
	}

	@Override
	public void validate(ValidateContext context, ValidateResult result) {
		for (String paramName : callbackMap.keySet()) {
			List<ValidateCallback> list = callbackMap.get(paramName);
			String paramValue = context.getParameter(paramName);
			for (ValidateCallback c : list) {
				c.valid(paramName, paramValue, result, context);
			}
			result.addValue(paramName, paramValue);
		}
	}

	protected abstract void addValidCondition(Map<String, List<ValidateCallback>> callbackMap);

	/**
	 * get ValidateCallback list, create if not exist
	 * 
	 * @param formFieldName
	 * @return
	 */
	protected List<ValidateCallback> getOrCreate(String formFieldName) {
		List<ValidateCallback> vcList = this.callbackMap.get(formFieldName);
		if (vcList == null) {
			vcList = new ArrayList<ValidateCallback>();
			this.callbackMap.put(formFieldName, vcList);
		}

		return vcList;
	}
}
