/**
 * 
 */
package cjlite.web.core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.inject.Binder;
import com.google.inject.Key;
import com.google.inject.Scopes;
import com.google.inject.internal.UniqueAnnotations;

import cjlite.app.UncheckedException;
import cjlite.app.core.AppBinderImpl;
import cjlite.config.Config;
import cjlite.log.Logger;
import cjlite.utils.Lists;
import cjlite.utils.Maps;
import cjlite.utils.Strings;
import cjlite.web.ConfigFolder;
import cjlite.web.WebBinder;
import cjlite.web.WebConfig;
import cjlite.web.handler.ControllerHandler;
import cjlite.web.handler.Handler;
import cjlite.web.helper.ControllerHelper;
import cjlite.web.helper.WebConfigHelper;
import cjlite.web.interceptor.ControllerInterceptor;
import cjlite.web.mvc.ControllerDefinition;

/**
 * @author kevin
 * 
 */
public class WebBinderImpl extends AppBinderImpl implements WebBinder {

	private static Logger logger = Logger.thisClass();

	//
	private List<Class<? extends Handler>> handlerClassList = Lists.newArrayList();

	//
	private List<Package> controllerPackageList = Lists.newArrayList();

	private List<Class<?>> controllerClassList = Lists.newArrayList();

	private Map<Package, List<Class<?>>> controllerPackageClassList = Maps.newHashMap();

	// Interceptors
	InterceptorCollector globalInterceptorCollector = new InterceptorCollector(InterceptorCollectorType.Global,
			"WebBinderImpl-Root");

	// config package used
	private List<Package> configPackageList = Lists.newArrayList();

	private Map<Package, List<Class<?>>> configPackageClassMap = Maps.newHashMap();

	public WebBinderImpl(Config config) {
		super(config);
	}

	/**
	 * Add Hanlder class which must be extends from Handler
	 * 
	 * @param handlerClass
	 */
	public void addHandler(Class<? extends Handler> handlerClass) {
		handlerClassList.add(handlerClass);
	}

	/**
	 * Add controller package, system would scan class with Controller annotation
	 * 
	 * @param controllerPackage
	 */
	public void addControllerPackage(Package controllerPackage) {
		if (this.controllerPackageClassList.containsKey(controllerPackage)) {
			logger.warn("Package'{0}' already been added", controllerPackage.getName());
			return;
		}
		controllerPackageList.add(controllerPackage);
		List<Class<?>> list = new ArrayList<Class<?>>();
		ControllerHelper.loadControllerClass(controllerPackage, list);
		if (list.size() == 0) {
			logger.warn("There is not controller class under package'{0}'", controllerPackage.getName());
		} else {
			controllerPackageClassList.put(controllerPackage, list);
		}
	}

	/**
	 * Add a controllerClass which must have type Controller annotation
	 * 
	 * @param controllerClass
	 */
	public void addController(Class<?> controllerClass) {
		controllerClassList.add(controllerClass);
	}

	@Override
	public void addInterceptor(Class<? extends ControllerInterceptor> interceptorClass) {
		globalInterceptorCollector.add(interceptorClass);
	}

	/**
	 * Install webConfig into this binder
	 * 
	 * @param webConfig
	 */
	public void install(WebConfig webConfig) {
		webConfig.config(this);
	}

	@Override
	public void configure(Binder binder) {
		// first config
		this.installWebConfigClass(binder);
		// then other
		super.configure(binder);
		bindHandlerClass(binder);
		bindControllerClass(binder);
		bindInterceptorClass(binder);
	}

	/**
	 * @param binder
	 */
	private void installWebConfigClass(Binder binder) {
		this.configPackageClassMap.forEach((k, v) -> {
			v.forEach(c -> {
				WebConfig wc = null;
				try {
					wc = (WebConfig) c.newInstance();
					this.install(wc);
				} catch (InstantiationException | IllegalAccessException e) {
					String msg = Strings.fillArgs("Error happen when install WebConfig Class[{0}]", c.getName());
					throw new UncheckedException(msg, e);
				}

			});
		});
	}

	private void bindHandlerClass(Binder binder) {
		for (Class<? extends Handler> hclass : this.handlerClassList) {
			Key<Handler> key = Key.get(Handler.class, UniqueAnnotations.create());
			binder.bind(key).to(hclass).in(Scopes.SINGLETON);
		}
		// at last, we need to bind controllerHandler into system core module
		Key<Handler> key = Key.get(Handler.class, UniqueAnnotations.create());
		binder.bind(key).to(ControllerHandler.class).in(Scopes.SINGLETON);
	}

	private void bindControllerClass(Binder binder) {

		// process controller under package
		for (Package cpackage : this.controllerPackageClassList.keySet()) {
			// Controller Class list
			List<Class<?>> controllerClasses = this.controllerPackageClassList.get(cpackage);
			// ControllerInterceptor class list
			// TODO future request
			// InterceptorCollector collector = new InterceptorCollector(InterceptorCollectorType.Package,
			// cpackage.getName(), this.globalInterceptorCollector);
			//
			// Class<? extends ControllerInterceptor>[] packageInterceptors = ControllerHelper
			// .parsePackageInterceptors(package);
			// collector.add(packageInterceptors);
			//
			bindControllerClass(binder, controllerClasses);
		}

		bindControllerClass(binder, this.controllerClassList);
	}

	private void bindPackageControllerClass(InterceptorCollector parent, Binder binder,
			List<Class<?>> controllerClasses) {
		for (Class<?> controllerClass : controllerClasses) {
			Key<ControllerDefinition> key = Key.get(ControllerDefinition.class, UniqueAnnotations.create());
			ControllerDefinition cd = ControllerHelper.parse(parent, controllerClass);
			binder.bind(key).toInstance(cd);
		}
	}

	private void bindControllerClass(Binder binder, List<Class<?>> controllerClasses) {
		for (Class<?> controllerClass : controllerClasses) {
			Key<ControllerDefinition> key = Key.get(ControllerDefinition.class, UniqueAnnotations.create());
			InterceptorCollector collector = new InterceptorCollector(InterceptorCollectorType.Package,
					controllerClass.getPackage().getName(), this.globalInterceptorCollector);
			Class<? extends ControllerInterceptor>[] packageInterceptors = ControllerHelper
					.parsePackageInterceptors(controllerClass.getPackage());
			collector.add(packageInterceptors);
			ControllerDefinition cd = ControllerHelper.parse(collector, controllerClass);
			binder.bind(key).toInstance(cd);
		}
	}

	private void bindInterceptorClass(Binder binder) {
		List<Class<? extends ControllerInterceptor>> allInterceptors = Lists.newArrayList();
		this.globalInterceptorCollector.collectWithChild(allInterceptors);
		for (Class<? extends ControllerInterceptor> interceptClass : allInterceptors) {
			Key<ControllerInterceptor> key = Key.get(ControllerInterceptor.class, UniqueAnnotations.create());
			binder.bind(key).to(interceptClass).in(Scopes.SINGLETON);
		}
	}

	@Override
	@Deprecated
	public void statics(String... staticPaths) {
		if (staticPaths.length == 1) {
			WebRoot().statics(staticPaths[0]);
		} else if (staticPaths.length > 1) {
			String[] paths = new String[staticPaths.length - 1];
			System.arraycopy(staticPaths, 1, paths, 0, staticPaths.length - 1);
			WebRoot().statics(staticPaths[0], paths);
		}

	}

	@Override
	public ConfigFolder ConfigFolder(String configKey) {
		return ConfigFolder(configKey.trim(), true);
	}

	@Override
	public ConfigFolder WebRoot() {
		return innnerConfigFolder("RootPath", true, true);
	}

	private ConfigFolder innnerConfigFolder(String configKey, boolean webRoot, boolean webAccess) {
		if ("RootPath".equals(configKey)) {
			webRoot = true;
		}
		ConfigFolder folder = config.getConfigFolder(configKey);
		if (folder == null) {
			folder = new ConfigFolder(config, configKey, webRoot, webAccess);
			config.addConfigFolder(folder);
		}

		return folder;
	}

	@Override
	public ConfigFolder ConfigFolder(String configKey, boolean webAccess) {
		return innnerConfigFolder(configKey.trim(), false, webAccess);
	}

	@Override
	public void scanConfigPackage(Package configPackage) {
		if (this.configPackageClassMap.containsKey(configPackage)) {
			logger.warn("Package'{0}' already been added", configPackage.getName());
			return;
		}
		this.configPackageList.add(configPackage);
		List<Class<?>> list = new ArrayList<Class<?>>();
		WebConfigHelper.loadConfigClass(configPackage, list);
		if (list.size() == 0) {
			logger.warn("There is no config class exist under package'{0}'", configPackage.getName());
		} else {
			configPackageClassMap.put(configPackage, list);
		}
	}

}
