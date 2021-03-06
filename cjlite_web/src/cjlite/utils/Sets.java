/**
 * 
 */
package cjlite.utils;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * @author kevin
 * 
 */
public class Sets {

	private Sets() {
	}

	// HashSet

	/**
	 * Creates an empty {@code HashSet} instance.
	 * 
	 * <p>
	 * <b>Note:</b> if {@code E} is an {@link Enum} type, use {@link EnumSet#noneOf} instead.
	 * 
	 * <p>
	 * <b>Note:</b> if you need an <i>immutable</i> empty Set, use {@link Collections#emptySet} instead.
	 * 
	 * @return a newly created, empty {@code HashSet}
	 */
	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	// TODO: Modify the sets returned by newConcurrentHashSet so calling
	// remove(null) on them doesn't throw an NPE.

	// LinkedHashSet

	/**
	 * Creates an empty {@code LinkedHashSet} instance.
	 * 
	 * @return a newly created, empty {@code LinkedHashSet}
	 */
	public static <E> LinkedHashSet<E> newLinkedHashSet() {
		return new LinkedHashSet<E>();
	}

	/*
	 * Regarding newSetForMap() and SetFromMap:
	 * 
	 * Written by Doug Lea with assistance from members of JCP JSR-166 Expert Group and released to the public domain,
	 * as explained at http://creativecommons.org/licenses/publicdomain
	 */

	/**
	 * Returns a set backed by the specified map. The resulting set displays the same ordering, concurrency, and
	 * performance characteristics as the backing map. In essence, this factory method provides a {@link Set}
	 * implementation corresponding to any {@link Map} implementation. There is no need to use this method on a
	 * {@link Map} implementation that already has a corresponding {@link Set} implementation (such as {@link HashMap}
	 * or {@link TreeMap}).
	 * 
	 * <p>
	 * Each method invocation on the set returned by this method results in exactly one method invocation on the backing
	 * map or its <tt>keySet</tt> view, with one exception. The <tt>addAll</tt> method is implemented as a sequence of
	 * <tt>put</tt> invocations on the backing map.
	 * 
	 * <p>
	 * The specified map must be empty at the time this method is invoked, and should not be accessed directly after
	 * this method returns. These conditions are ensured if the map is created empty, passed directly to this method,
	 * and no reference to the map is retained, as illustrated in the following code fragment:
	 * 
	 * <pre>
	 * 
	 * 
	 * 
	 * Set&lt;Object&gt; identityHashSet = Sets.newSetFromMap(new IdentityHashMap&lt;Object, Boolean&gt;());
	 * </pre>
	 * 
	 * This method has the same behavior as the JDK 6 method {@code Collections.newSetFromMap()}. The returned set is
	 * serializable if the backing map is.
	 * 
	 * @param map
	 *            the backing map
	 * @return the set backed by the map
	 * @throws IllegalArgumentException
	 *             if <tt>map</tt> is not empty
	 */
	public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
		return new SetFromMap<E>(map);
	}

	private static class SetFromMap<E> extends AbstractSet<E> implements Set<E>, Serializable {

		private final Map<E, Boolean> m; // The backing map
		private transient Set<E> s; // Its keySet

		SetFromMap(Map<E, Boolean> map) {
			m = map;
			s = map.keySet();
		}

		@Override
		public void clear() {
			m.clear();
		}

		@Override
		public int size() {
			return m.size();
		}

		@Override
		public boolean isEmpty() {
			return m.isEmpty();
		}

		@Override
		public boolean contains(Object o) {
			return m.containsKey(o);
		}

		@Override
		public boolean remove(Object o) {
			return m.remove(o) != null;
		}

		@Override
		public boolean add(E e) {
			return m.put(e, Boolean.TRUE) == null;
		}

		@Override
		public Iterator<E> iterator() {
			return s.iterator();
		}

		@Override
		public Object[] toArray() {
			return s.toArray();
		}

		@Override
		public <T> T[] toArray(T[] a) {
			return s.toArray(a);
		}

		@Override
		public String toString() {
			return s.toString();
		}

		@Override
		public int hashCode() {
			return s.hashCode();
		}

		@Override
		public boolean equals(Object object) {
			return this == object || this.s.equals(object);
		}

		@Override
		public boolean containsAll(Collection<?> c) {
			return s.containsAll(c);
		}

		@Override
		public boolean removeAll(Collection<?> c) {
			return s.removeAll(c);
		}

		@Override
		public boolean retainAll(Collection<?> c) {
			return s.retainAll(c);
		}

		// addAll is the only inherited implementation

		static final long serialVersionUID = 0;

		private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
			stream.defaultReadObject();
			s = m.keySet();
		}
	}

	/**
	 * Calculates and returns the hash code of {@code s}.
	 */
	static int hashCodeImpl(Set<?> s) {
		int hashCode = 0;
		for (Object o : s) {
			hashCode += o != null ? o.hashCode() : 0;
		}
		return hashCode;
	}

	public static <K> Set<K> newSetFromArray(K[] source) {
		Set<K> result = Sets.newHashSet();
		for (K k : source) {
			result.add(k);
		}
		return result;
	}

	public static <E> Set<E> copyOf(Set<E> sets) {
		return new HashSet<E>(sets);
	}
}
