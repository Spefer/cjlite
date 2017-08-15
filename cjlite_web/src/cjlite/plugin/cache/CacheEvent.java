package cjlite.plugin.cache;

import org.aopalliance.intercept.MethodInvocation;

public final class CacheEvent {

	private final String eventName;
	private final RefreshType eventType;
	private final Object source;
	private final NameMatcher nameMatcher;
	private final String sourceFrom;

	public CacheEvent(String name, String sourceFrom, RefreshType type, MethodInvocation invocation,
			NameMatcher nameMatcher) {
		this.eventName = name;
		this.eventType = type;
		this.sourceFrom = sourceFrom;
		this.source = invocation;
		this.nameMatcher = nameMatcher;
	}

	public String getName() {
		return this.eventName;
	}

	public RefreshType getType() {
		return this.eventType;
	}

	public Object getSourse() {
		return this.source;
	}

	public NameMatcher getNameMatcher() {
		return this.nameMatcher;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CacheEvent [eventName=").append(eventName);
		builder.append(", eventType=").append(eventType);
		builder.append(", nameMatcher=").append(nameMatcher);
		builder.append(", source at:").append(sourceFrom);
		builder.append("]");
		return builder.toString();
	}

}
