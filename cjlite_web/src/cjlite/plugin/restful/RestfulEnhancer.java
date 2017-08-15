/**
 * 
 */
package cjlite.plugin.restful;

import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import cjlite.utils.InvocationUtils;
import cjlite.utils.Strings;
import cjlite.web.mvc.RequestContext;

/**
 * @author YunYang
 * @version
 */
public class RestfulEnhancer implements MethodInterceptor {

	private static final String message = "message";

	private static final String msgSeperator = ";";

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		EntryRuleValidator validator = new EntryRuleValidator(invocation);
		if (!validator.isValidated()) {
			RequestContext context = InvocationUtils.getMethodParam(invocation, RequestContext.class);
			context.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			context.getResponse().setHeader(message, Strings.concatBySeperator(msgSeperator, validator.result()));
			return RestResult.New();
		}

		Object result = invocation.proceed();
		if (result == null) {
			RequestContext context = InvocationUtils.getMethodParam(invocation, RequestContext.class);
			context.getResponse().setStatus(HttpServletResponse.SC_FORBIDDEN);
			context.getResponse().setHeader(message, "Current Restful entry is not exist in system");
			return RestResult.New();
		}

		// returned value validation
		if (RestResult.class.isInstance(result) && validator.validateReturns((RestResult) result, invocation)) {
			RequestContext context = InvocationUtils.getMethodParam(invocation, RequestContext.class);
			context.getResponse().setStatus(HttpServletResponse.SC_BAD_REQUEST);
			context.getResponse().setHeader(message, Strings.concatBySeperator(msgSeperator, validator.result()));
			return RestResult.New();
		}
		// update status code from result
		if (RestResult.class.isInstance(result)) {
			RequestContext context = InvocationUtils.getMethodParam(invocation, RequestContext.class);
			ResultCode code = Optional.ofNullable(((RestResult) result).getResult()).orElse(ResultCode.OK);
			context.getResponse().setStatus(code.getCode());
		}

		return result;
	}

}
