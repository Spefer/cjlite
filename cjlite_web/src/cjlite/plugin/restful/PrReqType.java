/**
 * 
 */
package cjlite.plugin.restful;

/**
 * Parameter field require type
 * 
 * @author YunYang
 * @version
 */
public enum PrReqType {
	/**
	 * Exactly required
	 */
	Explicit("Required"),

	/**
	 * field is optional, have or no, either is fine
	 */
	Implicit("Optional");

	private String text;

	PrReqType(String text) {
		this.text = text;
	}

	public String getText() {
		return this.text;
	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append(this.name());
		b.append("(");
		b.append(this.text);
		b.append(")");
		return b.toString();
	}
}
