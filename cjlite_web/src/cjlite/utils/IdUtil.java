package cjlite.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * @author kevin
 *
 */
public class IdUtil {

	private static Random random = new Random();
	private static SecureRandom srandom = new SecureRandom();

	/**
	 * create a random string according to system.nanotime, that should be a fake random
	 * 
	 * @return xxxxxxxx-xxxxxxxx
	 */
	public static String genId16() {
		int length = 16;
		String s1 = Long.toHexString(System.nanoTime());
		int rn = random.nextInt();
		StringBuilder sb = new StringBuilder(17);
		String s2 = Integer.toHexString(rn);
		sb.append(s1);
		sb.append(s2, s2.length() - (length - s1.length()), s2.length());
		return sb.insert(8, '-').toString();
	}

	/**
	 * create a random string according to system.nanotime, that should be a fake random
	 * 
	 * @return xxxxxxxxxxxxxxxx
	 */
	public static String genId16chars() {
		int length = 16;
		String s1 = Long.toHexString(System.nanoTime());
		int rn = random.nextInt();
		StringBuilder sb = new StringBuilder(16);
		String s2 = Integer.toHexString(rn);
		sb.append(s1);
		sb.append(s2, s2.length() - (length - s1.length()), s2.length());
		return sb.toString();
	}

	/**
	 * 长度为8 的随机密码
	 * 
	 * @return
	 */
	public static String nextRandomPassword8() {
		return nextRandomPassword(8);
	}

	/**
	 * @return
	 */
	public static String nextRandomPassword(int length) {
		StringBuilder builder = new StringBuilder();
		builder.append(new BigInteger(64, srandom).toString(32));
		while (builder.length() < length) {
			builder.append(new BigInteger(64, srandom).toString(32));
		}

		return builder.substring(0, length).toLowerCase();
	}

	/**
	 * 62进制的nanotime
	 * 
	 * @return
	 */
	public static String time62() {
		long time = System.nanoTime();
		while (System.nanoTime() == time) {
		}
		BigInteger bi = BigInteger.valueOf(time);
		String result = NumberSystemCalculate.toAnyConversion(bi, 62);
		return result;
	}

	public static void main(String[] args) {
		// viewId();
		// viewPasswd();
		view62Id();
	}

	private static void view62Id() {
		Set<String> idSet = new HashSet<String>();
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < 1000; i++) {
			String id = time62();
			idSet.add(id);
			builder.append(id).append("\n");
//			String uuid=UUID.randomUUID().toString().replace("-","");
//			System.out.println(uuid+":"+NumberSystemCalculate.anyToAny(uuid, 8, 62));
		}
		System.out.println("set len: " + idSet.size());
		System.out.println(builder.toString());
	}

	public static void viewId() {
		StringBuilder builder = new StringBuilder();
		Set<String> idSet = new HashSet<String>();
		for (int i = 0; i < 1000; i++) {
			String id = genId16();
			idSet.add(id);
			builder.append(id).append("\n");
		}
		System.out.println("set len: " + idSet.size());
		System.out.println(builder.toString());
	}

	public static void viewPasswd() {
		StringBuilder builder = new StringBuilder();
		Set<String> idSet = new HashSet<String>();
		for (int i = 0; i < 1000; i++) {
			String id = nextRandomPassword8();
			idSet.add(id);
			builder.append(id).append("\n");
		}
		System.out.println("pass len: " + idSet.size());
		System.out.println(builder.toString());
	}

}
