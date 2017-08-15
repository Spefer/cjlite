/**
 * 
 */
package cjlite.web.handler;

/**
 * @author YunYang
 * @version Jul 2, 2015 4:40:00 PM
 */
public class HandleException extends Exception {
	private static final long serialVersionUID = 1L;

	public HandleException(String errorMsg, Exception excepton) {
		super(errorMsg, excepton);
	}
}
