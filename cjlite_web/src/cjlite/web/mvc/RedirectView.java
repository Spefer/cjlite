/**
 * 
 */
package cjlite.web.mvc;

/**
 * Url redirect(exclude domain name)
 * 
 * @author YunYang
 * @version
 */
public class RedirectView extends View {

	private final String redirectUrl;

	public RedirectView(String _type, String rediectUrl) {
		super(_type, "");
		this.redirectUrl = rediectUrl;
	}

	public String getRedirectUrl() {
		return this.redirectUrl;
	}

}
