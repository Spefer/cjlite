package cjlite.web.mvc;

/**
 * @author kevin
 */
public final class MethodParameter {

	private final Class<?> paramType;
	private final String paramName;

	public MethodParameter(Class<?> _paramType, String _name) {
		this.paramType = _paramType;
		this.paramName = _name;
	}

	public Class<?> getParamType() {
		return this.paramType;
	}

	public String getParamName() {
		return this.paramName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("MethodParameter [paramType=");
		builder.append(paramType.getName());
		builder.append(", paramName=");
		builder.append(paramName);
		builder.append("]");
		return builder.toString();
	}

}
