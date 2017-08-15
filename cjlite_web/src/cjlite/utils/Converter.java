package cjlite.utils;

public interface Converter<T> {

	/**
	 * Convert a not-null String to specified object.
	 */
	T convert(String s) throws ConvertException;

}