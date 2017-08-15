/**
 * 
 */
package cjlite.utils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import cjlite.i18n.I18n;

/**
 * @author YunYang
 * @version
 */
public class StringsTest {

	static Locale locale = Locale.CHINA;

	@BeforeClass
	public static void beforeSetup() {
		I18n.build(locale);
	}

	@Test
	public void printOut() {
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance();
		dfs.getGroupingSeparator();
		System.out.println("dicimal group char: " + dfs.getGroupingSeparator());
		System.out.println("dicimal char: " + dfs.getDecimalSeparator());
	}

	@Test
	public void testLocale() {
		System.out.printf("locale: %s", locale);
		Assert.assertEquals(locale, I18n.getIntance().getLocale());
		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(I18n.getIntance().getLocale());
		Assert.assertEquals('.', dfs.getDecimalSeparator());
	}

	@Test
	public void digitalTest() {
		Assert.assertTrue(Strings.isAllDigital("123"));
		Assert.assertTrue(Strings.isAllDigital("1234567890"));
		Assert.assertTrue(Strings.isAllDigital("123.123"));
	}

	@Test
	public void digitalConcat() {
		String[] ss = { "aa", "bb", "cc" };
		Assert.assertEquals("'aa','bb','cc'", Strings.concatBySeperator(",", ss, s -> {
			return "'" + s + "'";
		}));
	}

}
