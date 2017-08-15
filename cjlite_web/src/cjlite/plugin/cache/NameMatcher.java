package cjlite.plugin.cache;

public enum NameMatcher {
	FullName, StartWith, EndWith;

	public boolean isNameMatch(String eventName, String targetName) {
		if (this == FullName) {
			return eventName.equals(targetName);
		} else if (this == StartWith) {
			return targetName.startsWith(eventName);
		} else if (this == EndWith) {
			return targetName.endsWith(eventName);
		}
		return false;
	}
}
