package cjlite.utils;

import java.math.BigInteger;

public class NumberSystemCalculate {

	/**
	 * 十进制转换中把字符转换为数
	 * 
	 * @param ch
	 * @return
	 */
	private static int changeDec(char ch) {
		int num = 0;
		if (ch >= 'A' && ch <= 'Z')
			num = ch - 'A' + 10;
		else if (ch >= 'a' && ch <= 'z')
			num = ch - 'a' + 36;
		else
			num = ch - '0';
		return num;
	}

	/**
	 * 任意进制转换为10进制
	 * 
	 * @param source
	 * @param base
	 * @return
	 */
	public static BigInteger toDecimal(String source, int base) {
		BigInteger Bigtemp = BigInteger.ZERO, temp = BigInteger.ONE;
		int len = source.length();
		for (int i = len - 1; i >= 0; i--) {
			if (i != len - 1)
				temp = temp.multiply(BigInteger.valueOf(base));
			int num = changeDec(source.charAt(i));
			Bigtemp = Bigtemp.add(temp.multiply(BigInteger.valueOf(num)));
		}
		return Bigtemp;
	}

	/**
	 * 数字转换为字符
	 * @param source
	 * @return
	 */
	private static char convertToNum(BigInteger source) {
		int n = source.intValue();

		if (n >= 10 && n <= 35)
			return (char) (n - 10 + 'A');

		else if (n >= 36 && n <= 61)
			return (char) (n - 36 + 'a');

		else
			return (char) (n + '0');
	}

	/**
	 * 十进制转换为任意进制
	 * 
	 * @param source
	 * @param base
	 * @return
	 */
	public static String toAnyConversion(BigInteger source, int base) {
		return toAnyConversion(source, new BigInteger(String.valueOf(base), 10));
	}

	/**
	 * 十进制转换为任意进制
	 * 
	 * @param source
	 * @param base
	 * @return
	 */
	public static String toAnyConversion(BigInteger source, BigInteger base) {
		String ans = "";
		while (source.compareTo(BigInteger.ZERO) != 0) {
			BigInteger temp = source.mod(base);
			source = source.divide(base);
			char ch = convertToNum(temp);
			ans = ch + ans;
		}
		return ans;
	}
	
	/**
	 * @param source
	 * @param scouceBase
	 * @param targetBase
	 */
	public static String anyToAny(String source, int scouceBase, int targetBase) {
		BigInteger decimalBI = toDecimal(source, scouceBase);
		return toAnyConversion(decimalBI, targetBase);
	}

	/**
	 * @param source
	 * @param scouceBase
	 * @param targetBase
	 */
	public static String anyToAny(String source, int scouceBase, BigInteger targetBase) {
		BigInteger decimalBI = toDecimal(source, scouceBase);
		return toAnyConversion(decimalBI, targetBase);
	}
}