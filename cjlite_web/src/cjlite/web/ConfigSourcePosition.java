/**
 * 
 */
package cjlite.web;

/**
 * @author YunYang Jan 22, 2016
 */
public class ConfigSourcePosition {

	private final String className;
	private final String sourceFileName;
	private final int lineNumber;
	private final String methodName;

	public ConfigSourcePosition(StackTraceElement source) {
		this.className = source.getClassName();
		this.sourceFileName = source.getFileName();
		this.lineNumber = source.getLineNumber();
		this.methodName = source.getMethodName();
	}

	@Override
	public String toString() {
		return "[" + className + "." + methodName + "()," + sourceFileName + ":" + lineNumber + "]";
	}

}
