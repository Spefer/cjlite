/**
 * 
 */
package cjlite.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author kevin
 */
public class Lists {

	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}


	public static <E> ArrayList<E> newArrayList(int initCap) {
		return new ArrayList<E>(initCap);
	}

	public static <E> CopyOnWriteArrayList<E> newCopyOnWriteArrayList() {
		return new CopyOnWriteArrayList<E>();
	}

	/**
	 * Returns an unmodifiable list containing the specified first element and the additional elements.
	 */
	public static <E> ArrayList<E> newArrayList(E first, E[] rest) {
		ArrayList<E> result = new ArrayList<E>(rest.length + 1);
		result.add(first);
		for (E element : rest) {
			result.add(element);
		}
		return result;
	}


	public static <E> List<E> newArrayList(E... ts) {
		ArrayList<E> list = new ArrayList<E>();
		Collections.addAll(list, ts);
		return list;
	}


	public static <E> ArrayList<E> newArrayList(Iterable<? extends E> elements) {
		// Let ArrayList's sizing logic work, if possible
		if (elements instanceof Collection) {
			Collection<? extends E> collection = (Collection<? extends E>) elements;
			return new ArrayList<E>(collection);
		} else {
			return newArrayList(elements.iterator());
		}
	}


	public static <E> ArrayList<E> newArrayList(Iterator<? extends E> elements) {
		ArrayList<E> list = newArrayList();
		while (elements.hasNext()) {
			list.add(elements.next());
		}
		return list;
	}
	
	

}
