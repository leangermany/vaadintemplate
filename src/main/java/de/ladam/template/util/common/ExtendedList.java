package de.ladam.template.util.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;
import java.util.function.Function;

public class ExtendedList<T> extends ArrayList<T> {
	private static final long serialVersionUID = 1547715396235986533L;

	public ExtendedList() {
	}

	public ExtendedList(Collection<T> initial) {
		super(initial);
	}

	public ExtendedList(@SuppressWarnings("unchecked") T... initial) {
		super(Arrays.asList(initial));
	}

	public T First() {
		if (this.size() > 0)
			return this.toArray()[0];

		else
			throw new NoSuchElementException("The collection has a size of 0.");
	}

	public T FirstOrDefault() {
		if (this.size() > 0)
			return this.toArray()[0];

		else
			return null;
	}

	public T Single() throws Exception {
		if (this.size() != 1)
			throw new Exception("There are none or more than one object in the list!");

		return this.First();
	}

	public T SingleOrDefault() {
		if (this.size() != 1)
			return null;

		return this.First();
	}

	public T SingleOrDefault(T defaultObject) {
		if (this.size() != 1)
			return defaultObject;

		return this.First();
	}

	public ExtendedList<T> Where(Function<T, Boolean> condition) {
		ExtendedList<T> filtered = new ExtendedList<T>();

		for (T object : this)
			if (condition.apply(object))
				filtered.add(object);

		return filtered;
	}

	public <Z> ExtendedList<T> Where(BiFunction<T, Z, Boolean> condition, List<Z> object2) {
		ExtendedList<T> filtered = new ExtendedList<T>();

		for (T o1 : this)
			for (Z o2 : object2) {
				if (condition.apply(o1, o2))
					filtered.add(o1);
			}

		return filtered;
	}

	public boolean Contains(Function<T, Boolean> condition) {

		for (T object : this)
			if (condition.apply(object))
				return true;

		return false;
	}

	public <Z> boolean Contains(BiFunction<T, Z, Boolean> condition, @SuppressWarnings("unchecked") Z... object2) {

		return Contains(condition, Arrays.asList(object2));
	}

	public <Z> boolean Contains(BiFunction<T, Z, Boolean> condition, List<Z> object2) {

		for (T o1 : this) {
			for (Z o2 : object2) {
				if (condition.apply(o1, o2))
					return true;
			}
		}

		return false;
	}

	public boolean containsAll(List<T> list) {
		int count = 0;
		for (T o1 : list) {
			if (contains(o1))
				count++;
		}
		return count == list.size();
	}

	public ArrayList<T> asArrayList() {
		return (ArrayList<T>) this;
	}

	public static <Z> ArrayList<Z> asArrayList(ExtendedList<Z> exList) {
		return exList.asArrayList();
	}

	public static <Z> ExtendedList<Z> asExtendedList(Collection<Z> collection) {
		ExtendedList<Z> l = new ExtendedList<Z>();
		if (collection != null)
			l.addAll(collection);
		return l;
	}

	public static <Z> ExtendedList<Z> asExtendedList(Z[] array) {
		ExtendedList<Z> l = new ExtendedList<Z>();
		if (array != null)
			l.addAll(Arrays.asList(array));
		return l;
	}

	@SuppressWarnings("unchecked")
	public T[] toArray() {
		return (T[]) super.toArray();
	}

}
