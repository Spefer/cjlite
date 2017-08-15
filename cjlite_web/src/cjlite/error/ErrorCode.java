/**
 * 
 */
package cjlite.error;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author YunYang
 * @version Jun 22, 2015 5:48:44 PM
 */
public final class ErrorCode {
	private static final DateFormat df = new SimpleDateFormat("yyyyMMdd.HHmmss");

	public static String gen() {
		Date date = Calendar.getInstance().getTime();
		String code = df.format(date);
		return code;
	}

	public static void main(String[] args) {
		System.out.println("code: " + gen());
	}
}
