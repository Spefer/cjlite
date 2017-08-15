package cjlite.utils;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import cjlite.log.Logger;

/**
 * @author kevin
 */
public final class TypeConverter {

	private static Logger logger = Logger.thisClass();

	private Map<Class<?>, Converter<?>> map = new HashMap<Class<?>, Converter<?>>();

	/**
	 * 
	 */
	public TypeConverter() {
		loadInternal();
	}

	private void loadInternal() {
		Converter<?> c = null;

		c = new BooleanConverter();
		map.put(boolean.class, c);
		map.put(Boolean.class, c);

		c = new CharacterConverter();
		map.put(char.class, c);
		map.put(Character.class, c);

		c = new ByteConverter();
		map.put(byte.class, c);
		map.put(Byte.class, c);

		c = new ShortConverter();
		map.put(short.class, c);
		map.put(Short.class, c);

		c = new IntegerConverter();
		map.put(int.class, c);
		map.put(Integer.class, c);

		c = new LongConverter();
		map.put(long.class, c);
		map.put(Long.class, c);

		c = new FloatConverter();
		map.put(float.class, c);
		map.put(Float.class, c);

		c = new DoubleConverter();
		map.put(double.class, c);
		map.put(Double.class, c);

		c = new TimestampConverter();
		map.put(Timestamp.class, c);
		map.put(Date.class, c);

		c = new SqlDateConverter();
		map.put(java.sql.Date.class, c);
	}

	public <T> void registerConvertor(Class<T> clazz, Converter<T> convertor) {
		map.put(clazz, convertor);
	}

	public boolean canConvert(Class<?> clazz) {
		return clazz.equals(String.class) || map.containsKey(clazz);
	}

	public <T> T convert(Class<T> clazz, String value) {
		Objects.requireNonNull(clazz, "The Type you want to convert to can not be null");
		Objects.requireNonNull(value, "The value you want to convert can not be null");
		Converter<?> c = map.get(clazz);
		if (c == null)
			return (T) value;
		try {
			return (T) c.convert(value);
		} catch (ConvertException e) {
			e.printStackTrace();
		}
		return null;
	}

	static Timestamp dateParse(String dateStr) throws ConvertException {
		if (dateStr == null || dateStr.trim().length() == 0) {
			return null;
		}

		String[] dates = dateStr.split("-");
		if (dates.length != 3) {
			return null;
		}
		try {
			int year = Integer.parseInt(dates[0]);
			int month = Integer.parseInt(dates[1]);
			int day = Integer.parseInt(dates[2]);
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, day, 0, 0, 0);
			return new Timestamp(calendar.getTimeInMillis());
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Timestamp", dateStr);
			throw new ConvertException(error, e);
		}
	}

	/**
	 * @param s
	 * @return
	 * @throws ConvertException
	 */
	public static java.sql.Date sqlDateParse(String dateStr) throws ConvertException {
		if (dateStr == null || dateStr.trim().length() == 0) {
			return null;
		}

		String[] dates = dateStr.split("-");
		if (dates.length != 3) {
			return null;
		}
		try {
			int year = Integer.parseInt(dates[0]);
			int month = Integer.parseInt(dates[1]);
			int day = Integer.parseInt(dates[2]);
			Calendar calendar = Calendar.getInstance();
			calendar.set(year, month - 1, day, 0, 0, 0);
			return new java.sql.Date(calendar.getTimeInMillis());
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Timestamp", dateStr);
			throw new ConvertException(error, e);
		}
	}

}

class BooleanConverter implements Converter<Boolean> {

	public Boolean convert(String s) {
		return Boolean.parseBoolean(s);
	}

}

class ByteConverter implements Converter<Byte> {

	public Byte convert(String s) {
		return Byte.parseByte(s);
	}

}

class CharacterConverter implements Converter<Character> {

	public Character convert(String s) {
		if (s.length() == 0)
			throw new IllegalArgumentException("Cannot convert empty string to char.");
		return s.charAt(0);
	}

}

class DoubleConverter implements Converter<Double> {

	public Double convert(String s) throws ConvertException {
		try {
			return Double.parseDouble(s);
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Double", s);
			throw new ConvertException(error, e);
		}
	}

}

class FloatConverter implements Converter<Float> {

	public Float convert(String s) throws ConvertException {
		try {
			return Float.parseFloat(s);
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Float", s);
			throw new ConvertException(error, e);
		}
	}

}

class IntegerConverter implements Converter<Integer> {

	public Integer convert(String s) throws ConvertException {
		try {
			return Integer.parseInt(s);
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Integer", s);
			throw new ConvertException(error, e);
		}
	}

}

class LongConverter implements Converter<Long> {

	public Long convert(String s) throws ConvertException {
		try {
			return Long.parseLong(s);
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Long", s);
			throw new ConvertException(error, e);
		}
	}

}

class ShortConverter implements Converter<Short> {

	public Short convert(String s) throws ConvertException {
		try {
			return Short.parseShort(s);
		} catch (Exception e) {
			String error = Strings.fillArgs("error on converting String value[{0}] to Short", s);
			throw new ConvertException(error, e);
		}
	}

}

class TimestampConverter implements Converter<Timestamp> {

	@Override
	public Timestamp convert(String s) throws ConvertException {
		Timestamp ts = null;
		try {
			ts = Timestamp.valueOf(s);
			return ts;
		} catch (Exception e) {
			ts = TypeConverter.dateParse(s);
		}
		return ts;
	}

}

class SqlDateConverter implements Converter<java.sql.Date> {

	@Override
	public java.sql.Date convert(String s) throws ConvertException {
		try {
			return java.sql.Date.valueOf(s);
		} catch (Exception e) {
			return TypeConverter.sqlDateParse(s);
		}
	}

}