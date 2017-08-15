/**
 * 
 */
package cjlite.plugin.restful;

import java.util.Collection;

import org.aopalliance.intercept.MethodInvocation;

import cjlite.error.ErrorMsg;
import cjlite.utils.InvocationUtils;
import cjlite.utils.Strings;
import cjlite.web.mvc.RequestContext;

/**
 * @author YunYang
 * @version
 */
public class EntryRuleValidator {

	private static final String entryParamRequired = "Entry Param[{0}] can not be null";

	private static final String returnValueRequired = "Should have a value for key[{0}], but currently not exist.";

	private boolean validated = true;

	private ErrorMsg error = new ErrorMsg();

	/**
	 * @param invocation
	 */
	public EntryRuleValidator(MethodInvocation invocation) {

		this.parse(invocation);
	}

	/**
	 * @param invocation
	 */
	private void parse(MethodInvocation invocation) {
		EntryRule rule = invocation.getMethod().getAnnotation(EntryRule.class);
		RequestContext context = InvocationUtils.getMethodParam(invocation, RequestContext.class);
		EntryParam[] params = rule.value();
		for (EntryParam param : params) {
			validate(param, context);
		}

		if (error.hasError()) {
			validated = false;
		}
	}

	/**
	 * @param param
	 * @param context
	 */
	private void validate(EntryParam param, RequestContext context) {
		String value = context.getParameter(param.name());
		if (value == null && param.req() == PrReqType.Explicit) {
			String msg = Strings.fillArgs(entryParamRequired, param.name());
			this.error.add(msg);
			return;
		}
	}

	/**
	 * @return
	 */
	public boolean isValidated() {
		return validated;
	}

	/**
	 * @return
	 */
	public String[] result() {
		Collection<String> msgs = error.getErrorMsgs();
		return msgs.toArray(new String[msgs.size()]);
	}

	/**
	 * @param result
	 * @param invocation
	 * @return
	 */
	public boolean validateReturns(RestResult result, MethodInvocation invocation) {

		EntryRule rule = invocation.getMethod().getAnnotation(EntryRule.class);
		String[] returnKeys = rule.returns();
		for (String key : returnKeys) {
			if (!result.getModelMap().containsKey(key)) {
				error.add(Strings.fillArgs(returnValueRequired, key));
			}
		}

		return error.hasError();
	}

}
