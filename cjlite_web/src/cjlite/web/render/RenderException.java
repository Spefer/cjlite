/**
 * 
 */
package cjlite.web.render;



/**
 * @author YunYang
 * @version Jun 22, 2015 5:00:38 PM 
 */
public class RenderException extends Exception {

	private static final long serialVersionUID = 1L;

	public RenderException(String msg, Exception e) {
		super(msg, e);
	}

	public RenderException(String msg) {
		super(msg);
	}

}
