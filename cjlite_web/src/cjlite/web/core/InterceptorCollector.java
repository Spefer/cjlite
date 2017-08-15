/**
 * 
 */
package cjlite.web.core;

import java.util.List;

import cjlite.utils.Lists;
import cjlite.web.interceptor.ControllerInterceptor;

/**
 * @author kevin
 * 
 */
public class InterceptorCollector {

	private final InterceptorCollector parent;
	private List<InterceptorCollector> childList;
	private List<Class<? extends ControllerInterceptor>> interceptorClassList;
	private final String name;
	private final InterceptorCollectorType type;

	public InterceptorCollector(InterceptorCollectorType type, String name) {
		this(type, name, null);
	}

	public InterceptorCollector(InterceptorCollectorType type, String name, InterceptorCollector parent) {
		this.type = type;
		this.name = name;
		this.parent = parent;
		this.childList = Lists.newArrayList();
		this.interceptorClassList = Lists.newArrayList();
		if (this.parent != null) {
			this.parent.addChild(this);
		}
	}

	private void addChild(InterceptorCollector interceptorCollector) {
		this.childList.add(interceptorCollector);
	}

	/**
	 * this would remove those duplicated one which been added in first
	 * 
	 * @param interceptorList
	 */
	public void add(List<Class<? extends ControllerInterceptor>> interceptorList) {
		interceptorClassList.addAll(interceptorList);
	}

	/**
	 * this would remove those duplicated one which been added in first
	 * 
	 * @param interceptors
	 */
	public void add(Class<? extends ControllerInterceptor>[] interceptors) {
		if (interceptors == null) {
			return;
		}
		for (Class<? extends ControllerInterceptor> interceptorClass : interceptors) {
			interceptorClassList.add(interceptorClass);
		}
	}

	public void collectWithParent(List<Class<? extends ControllerInterceptor>> list) {
		if (this.parent != null) {
			this.parent.collectWithParent(list);
		}
		list.addAll(interceptorClassList);
	}

	public void add(Class<? extends ControllerInterceptor> interceptorClass) {
		interceptorClassList.add(interceptorClass);
	}

	public void collectWithChild(List<Class<? extends ControllerInterceptor>> allInterceptors) {
		allInterceptors.addAll(this.interceptorClassList);
		for (InterceptorCollector collector : childList) {
			collector.collectWithChild(allInterceptors);
		}
	}

	@Override
	public String toString() {
		return "InterceptorCollector [type=" + type + ", name=" + name + ", parent =" + parent + "]";
	}
	
	

}
