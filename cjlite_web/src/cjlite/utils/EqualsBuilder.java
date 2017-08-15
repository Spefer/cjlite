package cjlite.utils;

import java.util.ArrayList;
import java.util.List;

public final class EqualsBuilder {

	private List<Object> sourceList = new ArrayList<Object>();
	private List<Object> targetList = new ArrayList<Object>();

	public EqualsBuilder() {

	}

	public EqualsBuilder append(Object source, Object target) {
		sourceList.add(source);
		targetList.add(target);
		return this;
	}

	public boolean isEquals() {
		int length = sourceList.size();
		for (int i = 0; i < length; i++) {
			Object source = this.sourceList.get(i);
			Object target = this.targetList.get(i);
			if (source != null && !source.equals(target)) {
				return false;
			}
		}
		return true;
	}

}
