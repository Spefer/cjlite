/**
 * 
 */
package cjlite.web.core;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;

import com.google.inject.Binding;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;

import cjlite.config.Config;
import cjlite.web.handler.ControllerHandler;
import cjlite.web.handler.Handler;
import cjlite.web.handler.HandlerManager;

/**
 * @author kevin
 * 
 */
public class HandlerManagerProvider implements Provider<HandlerManager> {

	private static final TypeLiteral<Handler> handlerType = TypeLiteral.get(Handler.class);

	@Inject
	private Injector injector;

	@Override
	public HandlerManager get() {
		List<Handler> handlerList = new ArrayList<Handler>();

		Handler controllerHandler = null;

		List<Binding<Handler>> bindings = injector.findBindingsByType(handlerType);
		for (Binding<Handler> handlerBinding : bindings) {
			Handler handler = handlerBinding.getProvider().get();
			if (ControllerHandler.class.equals(handler.getClass())) {
				controllerHandler = handler;
			} else {
				handlerList.add(handler);
			}
			handler.initial();
		}
		// add Controller Hanlder to the tail
		if (controllerHandler != null) {
			handlerList.add(controllerHandler);
		}

		Config config = this.injector.getInstance(Config.class);

		return new HandlerManagerImpl(config, handlerList.toArray(new Handler[handlerList.size()]));
	}
}
