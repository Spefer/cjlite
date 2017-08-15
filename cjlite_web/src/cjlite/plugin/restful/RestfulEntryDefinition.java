/**
 * 
 */
package cjlite.plugin.restful;

import cjlite.utils.Strings;
import cjlite.web.handler.UriPath;

/**
 * @author YunYang
 * @version
 */
public class RestfulEntryDefinition {

	private static final String entryHtml = "<div class=\"entry\"><div class=\"router\">{0}<span class=\"method\">[{1}]</span></div>{2}<div class=\"item-title\">Entry Request Parameters:</div><ul>{3}</ul> <div class=\"item-title\">Entry Return Values should have:</div><ul>{4}</ul></div>";

	private static final String ruleDesc = "<div class=\"desc\">{0}</div>";

	private static final String ruleLi = "<li><span class=\"field\">{0}</span> : {1}</li>";

	private static final String noRuleDifn = "<li class=\"no-rule\">No Rule definition now</li>";

	private static final String ruleReturnLi = " <li><span class=\"field\">{0}</span></li>";

	private static final String noReturnDifn = "<li class=\"no-rule\">No return value definition required</li>";

	private UriPath path;

	private String method;

	private EntryRule rule;

	/**
	 * @param k
	 * @param m
	 * @param rule
	 */
	public RestfulEntryDefinition(UriPath path, String method, EntryRule rule) {
		this.path = path;
		this.method = method;
		this.rule = rule;
	}

	public String toHtml() {
		String desc = this.rule != null && this.rule.desc().length() > 0 ? Strings.fillArgs(ruleDesc, this.rule.desc())
				: "";

		StringBuilder params = new StringBuilder();
		if (this.rule != null && rule.value().length > 0) {
			for (EntryParam ep : rule.value()) {
				params.append(Strings.fillArgs(ruleLi, ep.name(), ep.req().getText()));
			}
		} else {
			params.append(noRuleDifn);
		}

		StringBuilder returns = new StringBuilder();
		if (this.rule != null && rule.returns().length > 0) {
			for (String key : rule.returns()) {
				returns.append(Strings.fillArgs(ruleReturnLi, key));
			}
		} else {
			returns.append(noReturnDifn);
		}

		return Strings.fillArgs(entryHtml, this.path.path(), this.method, desc, params.toString(), returns.toString());
	}

}
