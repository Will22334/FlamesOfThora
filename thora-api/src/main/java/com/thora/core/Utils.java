package com.thora.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class Utils {

	public static class Timer {

		public static final int DEFAULT_HISTORY_SIZE = 4;
		public static final int DEFAULT_HISTORY_GROWTH_SIZE = 0;

		public static String pretty(long time) {
			return time + "ms";
		}

		private AtomicBoolean atomicStarted = new AtomicBoolean();
		private boolean started;
		private long startTime;
		private final List<Long> marks, safeMarks;

		public Timer() {
			this(DEFAULT_HISTORY_SIZE, DEFAULT_HISTORY_GROWTH_SIZE);
		}

		public Timer(int initialSize, int growthIncrement) {
			marks = new Vector<>(initialSize, growthIncrement);
			safeMarks = Collections.unmodifiableList(marks);
		}

		protected long getCurrentTime() {
			return System.currentTimeMillis();
		}

		public final boolean hasStarted() {
			return started;
		}

		public long getStartTime() {
			return startTime;
		}

		public Timer start() {
			if(atomicStarted.compareAndSet(false, true)) {
				startTime = getCurrentTime();
				started = true;
			}
			return this;
		}

		public long mark() {
			if(!hasStarted())
				throw new IllegalStateException(this + " cannot call mark() until start() is called!");
			long timeDiff = getCurrentTime() - getStartTime();
			marks.add(timeDiff);
			return timeDiff;
		}

		public String prettyMark() {
			return pretty(mark());
		}

		public long nextMark() {
			if(!hasStarted())
				throw new IllegalStateException(this + " cannot call mark() until start() is called!");
			long timeDiff = getCurrentTime() - getStartTime();
			marks.add(timeDiff);
			if(marks.size() > 1)
				timeDiff -= marks.get(marks.size()-2);
			return timeDiff;
		}

		public String nextPrettyMark() {
			return pretty(nextMark());
		}

		public List<Long> getMarks() {
			return safeMarks;
		}

		public Stream<Long> marks() {
			return getMarks().stream();
		}

		public long getMark(int occurrence) {
			return getMarks().get(occurrence);
		}

		public Collection<Long> getMarkTimes() {
			return markTimes()
					.collect(Collectors.toList());
		}

		public Stream<Long> markTimes() {
			return marks()
					.map(this::computeMarkDifference);
		}

		protected final long computeMarkDifference(long mark) {
			return mark - startTime;
		}

		public long getMarkTime(int occurrence) {
			return getMark(occurrence) - startTime;
		}

	}

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
	 * @param condition the condition.
	 * @param message the exception message.
	 */
	public static void assertArg(boolean condition, String message) {
		if(condition) throw new IllegalArgumentException(message);
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
	
	public static final <A,R> Consumer<A> consume(final Function<A,R> f, final A arg) {
		return (a) -> f.apply(a);
	}
	
	public static final <A,B,R> BiConsumer<A,B> consume(final BiFunction<A,B,R> f) {
		return (a1,a2) -> f.apply(a1, a2);
	}
	
	public static final <A,B,C,R> TriConsumer<A,B,C> consume(final TriFunction<A,B,C,R> f) {
		return (a1,a2,a3) -> f.apply(a1, a2, a3);
	}
	
	public static final <A,R> Supplier<R> bindArg(final Function<A,R> f, final A arg) {
		return () -> f.apply(arg);
	}
	
	public static final <R> Supplier<R> bindArg(final IntFunction<R> f, final int arg) {
		return () -> f.apply(arg);
	}
	
	public static final <A,B> Consumer<B> bindArg1(final BiConsumer<A,B> f, final A arg) {
		return (b) -> f.accept(arg, b);
	}
	
	public static final <A,B,R> Function<B,R> bindArg1(final BiFunction<A,B,R> f, final A arg) {
		return (b) -> f.apply(arg, b);
	}
	
	public static final <A,B> Consumer<A> bindArg2(final BiConsumer<A,B> f, final B arg) {
		return (a) -> f.accept(a, arg);
	}
	
	public static final <A,B,R> Function<A,R> bindArg2(final BiFunction<A,B,R> f, final B arg) {
		return (a) -> f.apply(a, arg);
	}
	
	@FunctionalInterface
	public static interface IntObjFunction<A,R> {
		public R apply(int i, A a);
	}
	
	@FunctionalInterface
	public static interface IntIntObjFunction<A,R> {
		public R apply(int i1, int i2, A a);
	}
	
	@FunctionalInterface
	public static interface IntObjObjFunction<A,B,R> {
		public R apply(int i, A a, B b);
	}
	
	@FunctionalInterface
	public static interface IntObjConsumer<A> {
		public void accept(int i, A a);
	}
	
	@FunctionalInterface
	public static interface IntObjObjConsumer<A,B> {
		public void accept(int i, A a, B b);
	}
	
	@FunctionalInterface
	public static interface TriConsumer<A,B,C> {
		public void accept(A a, B b, C c);
	}
	
	@FunctionalInterface
	public static interface TriFunction<A,B,C,R> {
		public R apply(A a, B b, C c);
	}
	
	public static final String newThreadDumpMessage() {
		StringBuilder sb = new StringBuilder();
		Collection<Entry<Thread, StackTraceElement[]>> threads = Thread.getAllStackTraces().entrySet();
		for(Entry<Thread, StackTraceElement[]> e: threads) {
			final Thread t = e.getKey();
			final StackTraceElement[] stackTraces = e.getValue();
			
			sb.append("Thread: ").append(t.getName())
			.append(" | PID: ").append(t.getId())
			.append(" | State: ").append(t.getState())
			.append(System.lineSeparator());
			
			for(int i=0; i<stackTraces.length; ++i) {
				final StackTraceElement traceElement = stackTraces[i];
				sb.append("\t").append(traceElement)
				.append(System.lineSeparator());
			}
		}

		return sb.toString();
	}
	
	public static final String getRenamedPackageClass(final Class<?> c, final String newClassName) {
		Objects.requireNonNull(c, "Cannot convert packaged name for a null class!");
		return c.getPackage().getName() + "." + newClassName;
	}

	public static final String simpleClassName(final Object o) {
		return o.getClass().getSimpleName();
	}

	public static IntStream reverseIntRange(final int from, final int to) {
		return IntStream.iterate(to, i -> i - 1)
				.limit(to - from);
	}

	public static <T> Stream<T> reverseIntRange(final T[] arr, final int from, final int to) {
		return reverseIntRange(from, to)
				.mapToObj(i -> arr[i]);
	}

	public static <T> Stream<T> reverseStream(final T[] arr) {
		return reverseIntRange(arr, 0, arr.length);
	}
	
	private Utils() {}
	
}
