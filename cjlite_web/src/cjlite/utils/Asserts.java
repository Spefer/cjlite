package cjlite.utils;

import java.util.Collection;

public final class Asserts {

	/**
	 * set non-visible constructor
	 */
	private Asserts() {
	}


	/**
	 * @param object
	 */
	public static void isNull(Object object) {
		isNull(object, "[Assertion failed] - the object argument must be null");
	}


	/**
	 * @param _object
	 * @param message
	 */
	public static void isNull(Object _object, String message) {
		if (_object != null) {
			throw new NullPointerException(message);
		}
	}


	/**
	 * If given object is Null, it would throw a null pointer Exception
	 * 
	 * @param object
	 */
	public static void notNull(Object object) {
		notNull(object, "[Assertion failed] - the object argument can not be null");
	}


	/**
	 * If given object is Null, it would throw a null pointer Exception with give message
	 * 
	 * @param object
	 */
	public static void notNull(Object _object, String message) {
		if (_object == null) {
			throw new NullPointerException(message);
		}
	}


	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 * 
	 * @param expression
	 *            a boolean expression
	 * @throws IllegalArgumentException
	 *             if {@code expression} is false
	 */
	public static void checkArgument(boolean expression) {
		if (!expression) {
			throw new IllegalArgumentException();
		}
	}


	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 * 
	 * @param expression
	 *            a boolean expression
	 * @param errorMessage
	 *            the exception message to use if the check fails; will be converted to a string using
	 *            {@link String#valueOf(Object)}
	 * @throws IllegalArgumentException
	 *             if {@code expression} is false
	 */
	public static void checkArgument(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new IllegalArgumentException(String.valueOf(errorMessage));
		}
	}


	/**
	 * Ensures the truth of an expression involving one or more parameters to the calling method.
	 * 
	 * @param expression
	 *            a boolean expression
	 * @param errorMessageTemplate
	 *            a template for the exception message should the check fail. The message is formed by replacing each
	 *            {@code %s} placeholder in the template with an argument. These are matched by position - the first
	 *            {@code %s} gets {@code errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the
	 *            formatted message in square braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs
	 *            the arguments to be substituted into the message template. Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}.
	 * @throws IllegalArgumentException
	 *             if {@code expression} is false
	 * @throws NullPointerException
	 *             if the check fails and either {@code errorMessageTemplate} or {@code errorMessageArgs} is null (don't
	 *             let this happen)
	 */
	public static void checkArgument(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalArgumentException(format(errorMessageTemplate, errorMessageArgs));
		}
	}


	/**
	 * Ensures the truth of an expression involving the state of the calling instance, but not involving any parameters
	 * to the calling method.
	 * 
	 * @param expression
	 *            a boolean expression
	 * @throws IllegalStateException
	 *             if {@code expression} is false
	 */
	public static void checkState(boolean expression) {
		if (!expression) {
			throw new IllegalStateException();
		}
	}


	/**
	 * Ensures the truth of an expression involving the state of the calling instance, but not involving any parameters
	 * to the calling method.
	 * 
	 * @param expression
	 *            a boolean expression
	 * @param errorMessage
	 *            the exception message to use if the check fails; will be converted to a string using
	 *            {@link String#valueOf(Object)}
	 * @throws IllegalStateException
	 *             if {@code expression} is false
	 */
	public static void checkState(boolean expression, Object errorMessage) {
		if (!expression) {
			throw new IllegalStateException(String.valueOf(errorMessage));
		}
	}


	/**
	 * Ensures the truth of an expression involving the state of the calling instance, but not involving any parameters
	 * to the calling method.
	 * 
	 * @param expression
	 *            a boolean expression
	 * @param errorMessageTemplate
	 *            a template for the exception message should the check fail. The message is formed by replacing each
	 *            {@code %s} placeholder in the template with an argument. These are matched by position - the first
	 *            {@code %s} gets {@code errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the
	 *            formatted message in square braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs
	 *            the arguments to be substituted into the message template. Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}.
	 * @throws IllegalStateException
	 *             if {@code expression} is false
	 * @throws NullPointerException
	 *             if the check fails and either {@code errorMessageTemplate} or {@code errorMessageArgs} is null (don't
	 *             let this happen)
	 */
	public static void checkState(boolean expression, String errorMessageTemplate, Object... errorMessageArgs) {
		if (!expression) {
			throw new IllegalStateException(format(errorMessageTemplate, errorMessageArgs));
		}
	}


	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @return the non-null reference that was validated
	 * @throws NullPointerException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference) {
		if (reference == null) {
			throw new NullPointerException();
		}
		return reference;
	}


	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @param errorMessage
	 *            the exception message to use if the check fails; will be converted to a string using
	 *            {@link String#valueOf(Object)}
	 * @return the non-null reference that was validated
	 * @throws NullPointerException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference, Object errorMessage) {
		if (reference == null) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		return reference;
	}


	/**
	 * Ensures that an object reference passed as a parameter to the calling method is not null.
	 * 
	 * @param reference
	 *            an object reference
	 * @param errorMessageTemplate
	 *            a template for the exception message should the check fail. The message is formed by replacing each
	 *            {@code %s} placeholder in the template with an argument. These are matched by position - the first
	 *            {@code %s} gets {@code errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the
	 *            formatted message in square braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs
	 *            the arguments to be substituted into the message template. Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}.
	 * @return the non-null reference that was validated
	 * @throws NullPointerException
	 *             if {@code reference} is null
	 */
	public static <T> T checkNotNull(T reference, String errorMessageTemplate, Object... errorMessageArgs) {
		if (reference == null) {
			// If either of these parameters is null, the right thing happens anyway
			throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
		}
		return reference;
	}


	/**
	 * Ensures that an {@code Iterable} object passed as a parameter to the calling method is not null and contains no
	 * null elements.
	 * 
	 * @param iterable
	 *            the iterable to check the contents of
	 * @return the non-null {@code iterable} reference just validated
	 * @throws NullPointerException
	 *             if {@code iterable} is null or contains at least one null element
	 */
	public static <T extends Iterable<?>> T checkContentsNotNull(T iterable) {
		if (containsOrIsNull(iterable)) {
			throw new NullPointerException();
		}
		return iterable;
	}


	/**
	 * Ensures that an {@code Iterable} object passed as a parameter to the calling method is not null and contains no
	 * null elements.
	 * 
	 * @param iterable
	 *            the iterable to check the contents of
	 * @param errorMessage
	 *            the exception message to use if the check fails; will be converted to a string using
	 *            {@link String#valueOf(Object)}
	 * @return the non-null {@code iterable} reference just validated
	 * @throws NullPointerException
	 *             if {@code iterable} is null or contains at least one null element
	 */
	public static <T extends Iterable<?>> T checkContentsNotNull(T iterable, Object errorMessage) {
		if (containsOrIsNull(iterable)) {
			throw new NullPointerException(String.valueOf(errorMessage));
		}
		return iterable;
	}


	/**
	 * Ensures that an {@code Iterable} object passed as a parameter to the calling method is not null and contains no
	 * null elements.
	 * 
	 * @param iterable
	 *            the iterable to check the contents of
	 * @param errorMessageTemplate
	 *            a template for the exception message should the check fail. The message is formed by replacing each
	 *            {@code %s} placeholder in the template with an argument. These are matched by position - the first
	 *            {@code %s} gets {@code errorMessageArgs[0]}, etc. Unmatched arguments will be appended to the
	 *            formatted message in square braces. Unmatched placeholders will be left as-is.
	 * @param errorMessageArgs
	 *            the arguments to be substituted into the message template. Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}.
	 * @return the non-null {@code iterable} reference just validated
	 * @throws NullPointerException
	 *             if {@code iterable} is null or contains at least one null element
	 */
	public static <T extends Iterable<?>> T checkContentsNotNull(T iterable, String errorMessageTemplate,
			Object... errorMessageArgs) {
		if (containsOrIsNull(iterable)) {
			throw new NullPointerException(format(errorMessageTemplate, errorMessageArgs));
		}
		return iterable;
	}


	private static boolean containsOrIsNull(Iterable<?> iterable) {
		if (iterable == null) {
			return true;
		}

		if (iterable instanceof Collection) {
			Collection<?> collection = (Collection<?>) iterable;
			try {
				return collection.contains(null);
			} catch (NullPointerException e) {
				// A NPE implies that the collection doesn't contain null.
				return false;
			}
		} else {
			for (Object element : iterable) {
				if (element == null) {
					return true;
				}
			}
			return false;
		}
	}


	/**
	 * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size {@code size}. An
	 * element index may range from zero, inclusive, to {@code size}, exclusive.
	 * 
	 * @param index
	 *            a user-supplied index identifying an element of an array, list or string
	 * @param size
	 *            the size of that array, list or string
	 * @throws IndexOutOfBoundsException
	 *             if {@code index} is negative or is not less than {@code size}
	 * @throws IllegalArgumentException
	 *             if {@code size} is negative
	 */
	public static void checkElementIndex(int index, int size) {
		checkElementIndex(index, size, "index");
	}


	/**
	 * Ensures that {@code index} specifies a valid <i>element</i> in an array, list or string of size {@code size}. An
	 * element index may range from zero, inclusive, to {@code size}, exclusive.
	 * 
	 * @param index
	 *            a user-supplied index identifying an element of an array, list or string
	 * @param size
	 *            the size of that array, list or string
	 * @param desc
	 *            the text to use to describe this index in an error message
	 * @throws IndexOutOfBoundsException
	 *             if {@code index} is negative or is not less than {@code size}
	 * @throws IllegalArgumentException
	 *             if {@code size} is negative
	 */
	public static void checkElementIndex(int index, int size, String desc) {
		checkArgument(size >= 0, "negative size: %s", size);
		if (index < 0) {
			throw new IndexOutOfBoundsException(format("%s (%s) must not be negative", desc, index));
		}
		if (index >= size) {
			throw new IndexOutOfBoundsException(format("%s (%s) must be less than size (%s)", desc, index, size));
		}
	}


	/**
	 * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of size {@code size}. A
	 * position index may range from zero to {@code size}, inclusive.
	 * 
	 * @param index
	 *            a user-supplied index identifying a position in an array, list or string
	 * @param size
	 *            the size of that array, list or string
	 * @throws IndexOutOfBoundsException
	 *             if {@code index} is negative or is greater than {@code size}
	 * @throws IllegalArgumentException
	 *             if {@code size} is negative
	 */
	public static void checkPositionIndex(int index, int size) {
		checkPositionIndex(index, size, "index");
	}


	/**
	 * Ensures that {@code index} specifies a valid <i>position</i> in an array, list or string of size {@code size}. A
	 * position index may range from zero to {@code size}, inclusive.
	 * 
	 * @param index
	 *            a user-supplied index identifying a position in an array, list or string
	 * @param size
	 *            the size of that array, list or string
	 * @param desc
	 *            the text to use to describe this index in an error message
	 * @throws IndexOutOfBoundsException
	 *             if {@code index} is negative or is greater than {@code size}
	 * @throws IllegalArgumentException
	 *             if {@code size} is negative
	 */
	public static void checkPositionIndex(int index, int size, String desc) {
		checkArgument(size >= 0, "negative size: %s", size);
		if (index < 0) {
			throw new IndexOutOfBoundsException(format("%s (%s) must not be negative", desc, index));
		}
		if (index > size) {
			throw new IndexOutOfBoundsException(format("%s (%s) must not be greater than size (%s)", desc, index, size));
		}
	}


	/**
	 * Ensures that {@code start} and {@code end} specify a valid <i>positions</i> in an array, list or string of size
	 * {@code size}, and are in order. A position index may range from zero to {@code size}, inclusive.
	 * 
	 * @param start
	 *            a user-supplied index identifying a starting position in an array, list or string
	 * @param end
	 *            a user-supplied index identifying a ending position in an array, list or string
	 * @param size
	 *            the size of that array, list or string
	 * @throws IndexOutOfBoundsException
	 *             if either index is negative or is greater than {@code size}, or if {@code end} is less than
	 *             {@code start}
	 * @throws IllegalArgumentException
	 *             if {@code size} is negative
	 */
	public static void checkPositionIndexes(int start, int end, int size) {
		checkPositionIndex(start, size, "start index");
		checkPositionIndex(end, size, "end index");
		if (end < start) {
			throw new IndexOutOfBoundsException(format("end index (%s) must not be less than start index (%s)", end,
					start));
		}
	}


	/**
	 * Substitutes each {@code %s} in {@code template} with an argument. These are matched by position - the first
	 * {@code %s} gets {@code args[0]}, etc. If there are more arguments than placeholders, the unmatched arguments will
	 * be appended to the end of the formatted message in square braces.
	 * 
	 * @param template
	 *            a non-null string containing 0 or more {@code %s} placeholders.
	 * @param args
	 *            the arguments to be substituted into the message template. Arguments are converted to strings using
	 *            {@link String#valueOf(Object)}. Arguments can be null.
	 */
	// VisibleForTesting
	static String format(String template, Object... args) {
		// start substituting the arguments into the '%s' placeholders
		StringBuilder builder = new StringBuilder(template.length() + 16 * args.length);
		int templateStart = 0;
		int i = 0;
		while (i < args.length) {
			int placeholderStart = template.indexOf("%s", templateStart);
			if (placeholderStart == -1) {
				break;
			}
			builder.append(template.substring(templateStart, placeholderStart));
			builder.append(args[i++]);
			templateStart = placeholderStart + 2;
		}
		builder.append(template.substring(templateStart));

		// if we run out of placeholders, append the extra args in square braces
		if (i < args.length) {
			builder.append(" [");
			builder.append(args[i++]);
			while (i < args.length) {
				builder.append(", ");
				builder.append(args[i++]);
			}
			builder.append("]");
		}

		return builder.toString();
	}
}
