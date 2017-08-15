package cjlite.utils;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cjlite.i18n.I18n;

/**
 * @author kevin
 */
public class Strings {

	private static final String EMPTY = "";

	private Strings() {
	}

	/**
	 * Check give string if has given chars
	 * 
	 * @param str
	 * @param chars
	 * @return true if has give chars, otherwise false
	 */
	public static boolean hasChars(String str, char... chars) {
		char[] strChars = str.toCharArray();
		int i = 0;
		while (i < chars.length) {
			if (!hasChar(strChars, chars[i])) {
				return false;
			}
			i++;
		}

		return true;
	}

	private static boolean hasChar(char[] strChars, char c) {
		int i = 0;
		while (i < strChars.length) {
			if (strChars[i] == c) {
				return true;
			}
			i++;
		}
		return false;
	}

	public static boolean hasChar(String str, char c) {
		return hasChar(str.toCharArray(), c);
	}

	/**
	 * substring by give char
	 * 
	 * @param key
	 * @param start
	 * @param end
	 * @return
	 */
	public static String subString(String key, char start, char end) {
		char[] chars = key.toCharArray();
		int i = 0;
		int starC = -1, endC = -1;
		while (i < chars.length) {
			if (chars[i] == start) {
				starC = i;

			}
			if (chars[i] == end) {
				endC = i;
				break;
			}
			i++;
		}

		if (starC != -1 && endC != -1 && endC > starC) {
			// if (endC - starC) == 1, the sub string would be emtry;
			if ((endC - starC) == 1) {
				return "";
			}
			// otherwise,
			starC += 1;
			return new String(chars, starC, endC - starC);
		}

		// if endC<=starC, return null;
		return null;
	}

	/**
	 * ignore empty is true by default
	 * 
	 * @param str
	 * @param c
	 * @return
	 */
	public static String[] split(String str, char c) {
		return split(str, c, true);
	}

	/**
	 * if c is not exist in String, would return the whole string as a Array with 0 length
	 * 
	 * @param c
	 * @param ignoreEmpty
	 *            TODO
	 * @param patterns
	 * @return
	 */
	public static String[] split(String str, char c, boolean ignoreEmpty) {
		char[] chars = str.toCharArray();
		int lastOffset = -1;
		int i = 0;
		int count = 0;
		int capcity = 8;
		int step = 8;
		String[] target = new String[capcity];
		String temp = "";
		if (chars.length == 0) {
			return new String[0];
		}

		while (i < chars.length) {
			if (chars[i] == c && i < chars.length - 1) {

				if (i - lastOffset == 1) {
					temp = "";
				} else {
					temp = new String(chars, lastOffset + 1, i - lastOffset - 1);
				}

				// ignore empty
				if (ignoreEmpty) {
					if (!temp.isEmpty()) {
						target[count] = temp;
						count++;
					}
				} else {
					target[count] = temp;
					count++;
				}

				if (count >= capcity) {
					capcity += step;
					String[] newTarget = new String[capcity];
					System.arraycopy(target, 0, newTarget, 0, count);
					target = newTarget;
				}
				lastOffset = i;
			}

			if (i == chars.length - 1) {
				if (chars[i] == c) {
					temp = new String(chars, lastOffset + 1, i - lastOffset - 1);
					// ignore empty
					if (ignoreEmpty) {
						if (!temp.isEmpty()) {
							target[count] = temp;
						} else {
							count--;
						}
						break;
					} else {
						target[count] = temp;
						count++;
					}
					// expend string array below
					if (count >= capcity) {
						capcity += step;
						String[] newTarget = new String[capcity];
						System.arraycopy(target, 0, newTarget, 0, count);
						target = newTarget;
					}

					target[count] = "";
				} else {
					temp = new String(chars, lastOffset + 1, i - lastOffset);
					// ignore empty
					if (ignoreEmpty) {
						if (!temp.isEmpty()) {
							target[count] = temp;
						}
					} else {
						target[count] = temp;
					}
				}
				break;
			}
			i++;
		}

		String[] newTarget2 = new String[count + 1];
		System.arraycopy(target, 0, newTarget2, 0, count + 1);

		return newTarget2;
	}

	/**
	 * @param str
	 * @param c
	 * @return
	 */
	public static String subStringBefore(String str, char c) {
		char[] strChars = str.toCharArray();
		int i = 0;
		while (i < strChars.length) {
			if (strChars[i] == c) {
				break;
			}
			i++;
		}

		return new String(strChars, 0, i);
	}

	/**
	 * @param str
	 * @param c
	 * @return
	 */
	public static String subStringAfter(String str, char c) {
		char[] strChars = str.toCharArray();
		int i = 0;
		while (i < strChars.length) {
			if (strChars[i] == c) {
				break;
			}
			i++;
		}
		if (i == strChars.length) {
			return str;
		}

		return new String(strChars, i + 1, strChars.length - i - 1);
	}

	public static void toMap(String str, char delimiter, Map<String, String> map) {
		String[] arrays = split(str, delimiter);

		for (String str2 : arrays) {
			String te = str2.trim();
			if (te.length() >= 3) {
				String[] arrays2 = split(te, '=');
				if (arrays2.length == 2) {
					map.put(arrays2[0].trim(), arrays2[1].trim());
				}
			}
		}

	}

	/**
	 * @param chars
	 * @return
	 */
	public static char[] LeftTrim(char[] chars) {
		int i = 0;
		while (i < chars.length) {
			if (Character.isWhitespace(chars[i])) {
				i++;
				continue;
			}
			break;
		}
		if (i == chars.length) {
			return new char[0];
		}
		if (i == 0) {
			return chars;
		}
		char[] newchars = new char[chars.length - i];
		System.arraycopy(chars, i, newchars, 0, chars.length - i);
		return newchars;
	}

	/**
	 * Format string {0}.... by given args
	 * 
	 * @param msg
	 * @param args
	 * @return
	 */
	public static String fillArgs(String msg, Object... args) {
		return fillArgsInString(msg, args);
	}

	private static String fillArgsInString(String result, Object[] parseTxtArgs) {
		int paras = 0;
		String lookupString = "{0}";

		if (result.indexOf(lookupString) <= -1)
			return result;

		String value = "{Null}";

		while (true) {
			if (result.indexOf(lookupString) <= -1) {
				break;
			}

			if (parseTxtArgs != null && (paras < parseTxtArgs.length)) {
				value = String.valueOf(parseTxtArgs[paras]);
			} else {
				value = "{Null}";
			}
			result = result.replace(lookupString, value);
			paras++;
			lookupString = "{" + paras + "}";
		}

		return result;
	}

	public static String concat(Object... objects) {
		Asserts.notNull(objects, "The objects can not be NULL");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				sb.append("[NULL]");
			} else {
				sb.append(String.valueOf(objects[i]));
			}
		}
		return sb.toString();
	}

	public static String concatBySeperator(String seperator, Object... objects) {
		Asserts.notNull(objects, "The objects can not be NULL");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			if (objects[i] == null) {
				sb.append("[NULL]");
			} else {
				sb.append(String.valueOf(objects[i]));
			}

			if (i < objects.length - 1) {
				sb.append(seperator);
			}
		}
		return sb.toString();
	}

	public static String concatBySeperator(String seperator, Object[] objects, StringDecorator decorator) {
		Asserts.notNull(objects, "The objects can not be NULL");
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < objects.length; i++) {
			if (i > 0) {
				sb.append(seperator);
			}

			if (objects[i] == null) {
				sb.append("[NULL]");
			} else {
				sb.append(decorator.decorate(String.valueOf(objects[i])));
			}

		}
		return sb.toString();
	}

	/**
	 * Splits a string in several parts (tokens) that are separated by delimiter characters. Delimiter may contains any
	 * number of character and it is always surrounded by two strings.
	 * 
	 * @param src
	 *            source to examine
	 * @param delimiters
	 *            char array with delimiter characters
	 * 
	 * @return array of tokens
	 */
	public static String[] splitc(String src, char[] delimiters) {
		if ((delimiters.length == 0) || (src.length() == 0)) {
			return new String[] { src };
		}
		char[] srcc = src.toCharArray();

		int maxparts = srcc.length + 1;
		int[] start = new int[maxparts];
		int[] end = new int[maxparts];

		int count = 0;

		start[0] = 0;
		int s = 0, e;
		if (equalsOne(srcc[0], delimiters)) { // string starts with delimiter
			end[0] = 0;
			count++;
			s = findFirstDiff(srcc, 1, delimiters);
			if (s == -1) { // nothing after delimiters
				return new String[] { EMPTY, EMPTY };
			}
			start[1] = s; // new start
		}
		while (true) {
			// find new end
			e = findFirstEqual(srcc, s, delimiters);
			if (e == -1) {
				end[count] = srcc.length;
				break;
			}
			end[count] = e;

			// find new start
			count++;
			s = findFirstDiff(srcc, e, delimiters);
			if (s == -1) {
				start[count] = end[count] = srcc.length;
				break;
			}
			start[count] = s;
		}
		count++;
		String[] result = new String[count];
		for (int i = 0; i < count; i++) {
			result[i] = src.substring(start[i], end[i]);
		}
		return result;
	}

	/**
	 * Match if one character equals to any of the given character.
	 * 
	 * @return <code>true</code> if characters match any character from given array, otherwise <code>false</code>
	 */
	public static boolean equalsOne(char c, char[] match) {
		for (char aMatch : match) {
			if (c == aMatch) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds index of the first character in given array the differs from the given set of characters.
	 * 
	 * @return index of matched character or -1
	 */
	public static int findFirstDiff(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match) == false) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Finds index of the first character in given array the matches any from the given set of characters.
	 * 
	 * @return index of matched character or -1
	 */
	public static int findFirstEqual(char[] source, int index, char[] match) {
		for (int i = index; i < source.length; i++) {
			if (equalsOne(source[i], match) == true) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * Returns <code>true</code> if string contains only digits.
	 */
	public static boolean containsOnlyDigits(CharSequence string) {
		int size = string.length();
		for (int i = 0; i < size; i++) {
			char c = string.charAt(i);
			if (Character.isDigit(c) == false) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Returns <code>true</code> if string {@link #containsOnlyDigits(CharSequence) contains only digits} or signs plus
	 * or minus.
	 */
	public static boolean containsOnlyDigitsAndSigns(CharSequence string) {
		int size = string.length();
		for (int i = 0; i < size; i++) {
			char c = string.charAt(i);
			if ((Character.isDigit(c) == false) && (c != '-') && (c != '+')) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String s) {
		return s == null || s.trim().length() == 0;
	}

	public static int charCount(String string, char c) {
		char[] chars = string.toCharArray();
		int count = 0;
		for (int i = 0; i < chars.length; i++) {
			if (chars[i] == c) {
				count += 1;
			}
		}
		return count;
	}

	private static final Pattern pattern = Pattern.compile("<([^>]*)>");

	public static String filterHtml(String input) {
		if (isEmpty(input)) {
			return "";
		}
		Matcher matcher = pattern.matcher(input);
		StringBuffer sb = new StringBuffer();
		boolean result = matcher.find();
		while (result) {
			matcher.appendReplacement(sb, "");
			result = matcher.find();
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String removeChars(String source, char remove) {
		StringBuilder builder = new StringBuilder(source.length());

		char[] sources = source.toCharArray();

		for (int i = 0; i < sources.length; i++) {
			if (sources[i] != remove) {
				builder.append(sources[i]);
			}
		}

		return builder.toString();
	}

	/**
	 * @param source
	 * @param target
	 * @return
	 */
	public static boolean startWithIgnoreCase(String source, String target) {
		String newSource = source.toLowerCase();
		return newSource.startsWith(target.toLowerCase());
	}

	/**
	 * @param source
	 * @param target
	 * @return
	 */
	public static String subStringAfter(String source, String target) {
		int index = source.indexOf(target);
		return source.substring(index + target.length());
	}

	/**
	 * Is digit or Decimal
	 * 
	 * @param paramValue
	 * @return
	 */
	public static boolean isAllDigital(String value) {
		char[] chars = value.toCharArray();

		Locale locale = I18n.getIntance().getLocale();

		DecimalFormatSymbols dfs = DecimalFormatSymbols.getInstance(locale);

		for (char c : chars) {
			boolean isLegal = Character.isDigit(c) || c == dfs.getDecimalSeparator();
			if (!isLegal) {
				return false;
			}
		}

		return true;
	}

	/**
	 * Is pure digit or not
	 * 
	 * @param paramValue
	 * @return
	 */
	public static boolean isPureDigit(String value) {
		char[] chars = value.toCharArray();

		for (char c : chars) {
			boolean isLegal = Character.isDigit(c);
			if (!isLegal) {
				return false;
			}
		}

		return true;
	}
}
