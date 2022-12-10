package com.thora.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class Utils {
	
	public static void assertNull(Object o, String message) {
		if(o == null) throw new NullPointerException(message);
	}
	
	public static void assertNull(Object o, Supplier<String> messageSupplier) {
		if(o == null) throw new NullPointerException(messageSupplier.get());
	}
	
	public static void assertNullArg(Object o, String message) {
		assertArg(o == null, message);
	}
	
	/**
	 * Throws a new IllegalArgumentException if the given condition is true.
	 * @param bool the condition.
	 * @param message the exception message.
	 */
	public static void assertArg(boolean bool, String message) {
		if(bool) throw new IllegalArgumentException(message);
	}
	
	/**
	 * Throws a new IllegalArgumentException if the given condition is true.
	 * @param bool the condition
	 * @param messageSupplier Supplier for the message
	 */
	public static void assertArg(boolean bool, Supplier<String> messageSupplier) {
		if(bool) throw new IllegalArgumentException(messageSupplier.get());
	}
	
	public static <E> Collection<E> intersection(Collection<E> a, Collection<E> b) {
		Collection<E> intersection = new ArrayList<>();
		Collection<E> smaller = a, bigger = b;
		if(b.size() < a.size()) {
			smaller = b;
			bigger = a;
		}
		
		for(E e : smaller) {
			if(bigger.contains(e))
				intersection.add(e);
		}
		return intersection;
	}
	
	/**
	 * Returns a Stream that consists of only the elements from the fist 
	 * Collection and not the second.
	 * @param <E> element type
	 * @param core core collection
	 * @param removals elements to be ignored
	 * @return Stream of the elements that are in the core collection and not in removals.
	 */
	public static <E> Stream<E> missing(Collection<E> core, Collection<E> removals) {
		return core.stream()
				.filter(e -> !removals.contains(e));
	}
	
	public static <E> Collection<E> getMissing(Collection<E> c, Collection<E> removals) {
		return missing(c, removals).collect(Collectors.toList());
	}
	
	public static final <E> int empty(Iterable<E> all, Consumer<? super E> consumer) {
		int count = 0;
		Iterator<E> it = all.iterator();
		while(it.hasNext()) {
			E e = it.next();
			it.remove();
			consumer.accept(e);
			++count;
		}
		return count;
	}
	
	public static final <E> int empty(Queue<E> queue, Consumer<? super E> consumer) {
		int count = 0;
		E e;
		while((e = queue.poll()) != null) {
			consumer.accept(e);
			++count;
		}
		return count;
	}
	
	public static final <K,V> int empty(Map<K,V> map, Consumer<? super V> consumer) {
		int count = 0;
		Iterator<Entry<K,V>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<K,V> entry = it.next();
			consumer.accept(entry.getValue());
			it.remove();
			++count;
		}
		return count;
	}
	
	public static final <L,A,B> void trigger(Iterable<L> all, Function<L,BiPredicate<? super A,? super B>> mapping, A a, B b) {
		trigger(all, l -> {
			return mapping.apply(l).test(a, b);
		});
	}
	
	public static final <L,A> void trigger(Iterable<L> all, Function<L,Predicate<? super A>> mapping, A element) {
		Iterator<L> it = all.iterator();
		while(it.hasNext()) {
			L l = it.next();
			if(mapping.apply(l).test(element))
				it.remove();
		}
	}
	
	/**
	 * Calls a listener function over all elements in a Iterable.
	 * Each listener in the Iterable where they return {@code true} will be removed from the Iterable.
	 * @param <L> listener type
	 * @param all all listeners
	 * @param pred function to be called for each listener
	 * @return total listeners that were removed/unregistered
	 */
	public static final <L> int trigger(Iterable<L> all, Predicate<? super L> pred) {
		int count = 0;
		Iterator<L> it = all.iterator();
		while(it.hasNext()) {
			L l = it.next();
			if(pred.test(l)) {
				it.remove();
				++count;
			}
		}
		return count;
	}
	
	public static final <K,V> int trigger(Map<K,V> map, Predicate<? super V> pred) {
		int count = 0;
		Iterator<Entry<K,V>> it = map.entrySet().iterator();
		while(it.hasNext()) {
			Entry<K,V> entrty = it.next();
			if(pred.test(entrty.getValue())) {
				it.remove();
				++count;
			}
		}
		return count;
	}
	
	public static final <L extends BiPredicate<? super A,? super A>,A> void triggerChange(Iterable<L> all, A from, A to) {
		trigger(all, l -> l.test(from, to));
	}
	
	public static final <L,A> void triggerChange(Iterable<L> all, Function<L,BiPredicate<? super A,? super A>> mapping, A from, A to) { 
		trigger(all, l -> mapping.apply(l).test(from, to));
	}
	
	private Utils() {}
	
}