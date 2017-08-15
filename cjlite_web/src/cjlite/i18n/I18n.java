/**
 * 
 */
package cjlite.i18n;

import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author YunYang
 * @version
 */
public class I18n {

	private static I18n instance;

	private static final ReentrantLock lock = new ReentrantLock();

	private static Locale customLocale;

	private final Locale locale;

	/**
	 * @param locale
	 * 
	 */
	private I18n(Locale locale) {
		this.locale = locale;
	}

	public static I18n build(Locale locale) {
		if (instance == null) {
			lock.lock();
			customLocale = locale;
			instance = new I18n(locale);
			lock.unlock();
		}
		return instance;
	}

	public static I18n getIntance() {
		if (instance == null) {
			lock.lock();
			instance = new I18n(Optional.ofNullable(customLocale).orElse(Locale.getDefault()));
			lock.unlock();
		}
		return instance;
	}

	public Locale getLocale() {
		return this.locale;
	}

}
