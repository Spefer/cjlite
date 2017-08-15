package cjlite.web.interceptor;

import cjlite.web.mvc.ModelView;
import cjlite.web.mvc.Views;

public abstract class ModelFreemarkerViewInterceptor implements ControllerInterceptor {

	@Override
	public Object intercept(ControllerInvocation invocation) throws Exception {
		Object result = invocation.invoke();

		if (ModelView.class.isInstance(result)) {
			ModelView mv = (ModelView) result;
			if (Views.FreeMarker.equals(mv.getView().getType())) {
				afterInvoke(invocation, mv);
			}
		}

		return result;
	}

	protected abstract void afterInvoke(ControllerInvocation invocation, ModelView mv);
}
