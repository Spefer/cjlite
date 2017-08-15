/**
 * 
 */
package cjlite.app;

/**
 * @author YunYang
 * @version Jun 22, 2015 5:31:37 PM
 */
public class UncheckedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public UncheckedException(String errorMsg) {
		super(errorMsg);
	}

	public UncheckedException(String errorMsg, Exception exception) {
		super(errorMsg, exception);
	}

}
