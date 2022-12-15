package com.thora.core.world;

import static java.lang.Math.max;
import static java.lang.Math.min;

import java.io.Serializable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import javax.annotation.Nonnull;

import org.danilopianini.util.FlexibleQuadTree;

import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;

public class StreamIntQuadTree<E> implements IntSpatialIndex<E> {

	/**
	 * Default maximum number of entries per node.
	 */
	public static final int DEFAULT_CAPACITY = 10;
	private static final long serialVersionUID = 0L;
	private final Map<Child, StreamIntQuadTree<E>> children;
	private final Deque<QuadTreeEntry<E>> elements;
	private final int maxElements;
	private Rectangle2DInt bounds;
	private StreamIntQuadTree<E> parent;

	/**
	 * root is NOT consistent everywhere. It is only guaranteed to be consistent
	 * in the entry point node and in the current root.
	 */
	private StreamIntQuadTree<E> root;

	/**
	 * Builds a {@link StreamIntQuadTree} with the default node capacity.
	 */
	public StreamIntQuadTree() {
		this(DEFAULT_CAPACITY);
	}
	
	/**
	 * @param elemPerQuad maximum number of elements per quad
	 */
	public StreamIntQuadTree(final int elemPerQuad) {
		this(elemPerQuad, null, null);
	}
	
	private StreamIntQuadTree(
			final int minx, final int maxx,
			final int miny, final int maxy,
			final int elemPerQuad,
			final StreamIntQuadTree<E> rootNode,
			final StreamIntQuadTree<E> parentNode
			) {
		this(elemPerQuad, rootNode, parentNode);
		bounds = new Rectangle2DInt(minx, miny, maxx, maxy);
	}

	private StreamIntQuadTree(
			final int elemPerQuad,
			final StreamIntQuadTree<E> rootNode,
			final StreamIntQuadTree<E> parentNode
			) {
		if (elemPerQuad < 2) {
			throw new IllegalArgumentException("At least two elements per quadtree are required for this index to work properly");
		}
		elements = new ArrayDeque<>(DEFAULT_CAPACITY);
		children = new EnumMap<>(Child.class);
		maxElements = elemPerQuad;
		parent = parentNode;
		root = rootNode == null ? this : rootNode;
	}

	private int centerX() {
		return bounds.getCenterX();
	}

	private int centerY() {
		return bounds.getCenterY();
	}

	private boolean contains(final int x, final int y) {
		return bounds == null || bounds.contains(x, y);
	}

	private StreamIntQuadTree<E> create(
			final int minx,
			final int maxx,
			final int miny,
			final int maxy,
			final StreamIntQuadTree<E> father
			) {
		return new StreamIntQuadTree<>(minx, maxx, miny, maxy, getMaxElementsNumber(), root, father);
	}
	
	private void createChildIfAbsent(final Child c) {
		if(!children.containsKey(c)) {
			children.put(c, createChild(c));
		}
		//children.putIfAbsent(c, create(minX(c), maxX(c), minY(c), maxY(c), this));
	}
	
	private final StreamIntQuadTree<E> createChild(final Child c) {
		return create(minX(c), maxX(c), minY(c), maxY(c), this);
	}
	
	private void createParent(final int x, final int y) {
		/*
		 * Determine where the parent should be
		 */
		if (x < centerX()) {
			final int minx = 2 * minX() - maxX();
			if (y < centerY()) {
				/*
				 * This will be TR child of the new parent
				 */
				root = create(minx, maxX(), 2 * minY() - maxY(), maxY(), null);
				root.setChild(Child.TR, this);
			} else {
				/*
				 * This will be BR child of the new parent
				 */
				root = create(minx, maxX(), minY(), 2 * maxY() - minY(), null);
				root.setChild(Child.BR, this);
			}
		} else {
			final int maxx = 2 * maxX() - minX();
			if (y < centerY()) {
				/*
				 * This will be TL child of the new parent
				 */
				root = create(minX(), maxx, 2 * minY() - maxY(), maxY(), null);
				root.setChild(Child.TL, this);
			} else {
				/*
				 * This will be BL child of the new parent
				 */
				root = create(minX(), maxx, minY(), 2 * maxY() - minY(), null);
				root.setChild(Child.BL, this);
			}
		}
		/*
		 * A bit cryptic, but the root of the new root is the root itself.
		 * Otherwise, the root would point to the previous root.
		 */
		root.root = root;
		root.subdivide();
	}

	@Override
	public int getDimensions() {
		return 2;
	}

	/**
	 * @return the maximum number of elements per node
	 */
	public int getMaxElementsNumber() {
		return maxElements;
	}

	private boolean hasSpace() {
		return elements.size() < maxElements;
	}

	@Override
	public void insert(final E e, final int... pos) {
		assert pos.length == 2;
		insert(e, pos[0], pos[1]);
	}

	/**
	 * Same of {@link #insert(Object, int...)}, but with explicit parameters.
	 *
	 * @param e element
	 * @param x X
	 * @param y Y
	 */
	public void insert(final E e, final int x, final int y) {
		if (bounds == null) {
			if (hasSpace()) {
				insertNode(e, x, y);
				return;
			}
			int minx = Integer.MAX_VALUE;
			int miny = Integer.MAX_VALUE;
			int maxx = Integer.MIN_VALUE;
			int maxy = Integer.MIN_VALUE;
			for (final QuadTreeEntry<E> element : elements) {
				minx = min(minx, element.x);
				miny = min(miny, element.y);
				maxx = max(maxx, element.x);
				maxy = max(maxy, element.y);
			}
			assert minx != Integer.MAX_VALUE;
			assert miny != Integer.MAX_VALUE;
			assert maxx != Integer.MIN_VALUE;
			assert maxy != Integer.MIN_VALUE;
			bounds = new Rectangle2DInt(minx - 1, miny - 1, maxx + 1, maxy + 1);
		}
		/*
		 * I must insert starting from the root. If the root does not contain
		 * the coordinates, then the tree should be expanded upwards
		 */
		while (!root.contains(x, y)) {
			root.createParent(x, y);
			root = root.root;
		}
		root.insertHere(e, x, y);
	}

	private void insertHere(final E e, final int x, final int y) {
		if (hasSpace()) {
			insertNode(e, x, y);
		} else {
			if (children.isEmpty()) {
				subdivide();
			}
			selectChild(x, y).insertHere(e, x, y);
		}
	}

	private void insertNode(@Nonnull final E e, final int x, final int y) {
		assert elements.size() < maxElements : "Bug in " + getClass() + ". Forced insertion over the container size.";
		elements.push(new QuadTreeEntry<>(e, x, y));
	}

	private int maxX() {
		return bounds.getMaxX();
	}

	private int maxX(final Child c) {
		switch (c) {
		case TR:
		case BR:
			return maxX();
		case BL:
		case TL:
			return centerX();
		default:
			throw new IllegalStateException();
		}
	}

	private int maxY() {
		return bounds.getMaxY();
	}

	private int maxY(final Child c) {
		switch (c) {
		case BL:
		case BR:
			return centerY();
		case TR:
		case TL:
			return maxY();
		default:
			throw new IllegalStateException();
		}
	}

	private int minX() {
		return bounds.getMinX();
	}

	private int minX(final Child c) {
		switch (c) {
		case TR:
		case BR:
			return centerX();
		case BL:
		case TL:
			return minX();
		default:
			throw new IllegalStateException();
		}
	}

	private int minY() {
		return bounds.getMinY();
	}

	private int minY(final Child c) {
		switch (c) {
		case BL:
		case BR:
			return minY();
		case TR:
		case TL:
			return centerY();
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Same of {@link #move(Object, int[], int[])}, but with explicit
	 * parameters.
	 *
	 * @param e  the element
	 * @param sx the start x
	 * @param sy the start y
	 * @param fx the final x
	 * @param fy the final y
	 *
	 * @return true if the element is found and no error occurred
	 */
	public boolean move(final E e, final int sx, final int sy, final int fx, final int fy) {
		return moveFromNode(root, e, sx, sy, fx, fy);
	}

	@Override
	public boolean move(final E e, final int[] start, final int[] end) {
		assert start.length == 2;
		assert end.length == 2;
		return move(e, start[0], start[1], end[0], end[1]);
	}

	private boolean moveFromNode(
			final StreamIntQuadTree<E> root,
			final E e,
			final int sx, final int sy,
			final int fx, final int fy) {
		final QuadTreeEntry<E> toRemove = new QuadTreeEntry<>(e, sx, sy);
		for (StreamIntQuadTree<E> cur = root; cur.contains(sx, sy); cur = cur.selectChild(sx, sy)) {
			if (cur.elements.remove(toRemove)) {
				/*
				 * Node found.
				 */
				if (cur.contains(fx, fy)) {
					/*
					 * Moved within the same quadrant.
					 */
					cur.insertNode(e, fx, fy);
				} else if (
						cur.parent == null
						|| !cur.parent.contains(fx, fy)
						|| !cur.swapMostStatic(e, fx, fy)
						) {
					/*
					 * In case:
					 *  - we are the root
					 *  - we moved outside the parent's area
					 *  - the swapping operation failed
					 */
					insert(e, fx, fy);
				}
				return true;
			}
			if (cur.children.isEmpty()) {
				return false;
			}
		}
		return false;
	}

	/**
	 * Same of querying with arrays, but with explicit parameters.
	 *
	 * @param x1 Rectangle X coordinate of the first point
	 * @param y1 Rectangle Y coordinate of the first point
	 * @param x2 Rectangle X coordinate of the second point
	 * @param y2 Rectangle Y coordinate of the second point
	 *
	 * @return {@link List} of Objects in range.
	 */
	public List<E> query(final int x1, final int y1, final int x2, final int y2) {
		final List<E> result = new ArrayList<>();
		root.query(min(x1, x2), min(y1, y2), max(x1, x2), max(y1, y2), result);
		return result;
	}
	
	/**
	 * Same of querying with arrays, but with explicit parameters.
	 *
	 * @param x1 Rectangle X coordinate of the first point
	 * @param y1 Rectangle Y coordinate of the first point
	 * @param x2 Rectangle X coordinate of the second point
	 * @param y2 Rectangle Y coordinate of the second point
	 *
	 * @return {@link List} of Objects in range.
	 */
	public void queryConsume(final int x1, final int y1, final int x2, final int y2,
			final Consumer<? super E> consumer) {
		root.queryForEach(min(x1, x2), min(y1, y2), max(x1, x2), max(y1, y2), consumer);
	}
	
	private void queryForEach(// NOPMD: False positive
			final int sx,
			final int sy,
			final int fx,
			final int fy,
			final Consumer<? super E> consumer
			) {
		assert !(bounds == null && !children.isEmpty());
		if (bounds == null || bounds.intersects(sx, sy, fx, fy)) {
			for (final QuadTreeEntry<E> entry : elements) {
				if (entry.isIn(sx, sy, fx, fy)) {
					consumer.accept(entry.element);
				}
			}
			// If there are no children, this will skip them.
			for (final StreamIntQuadTree<E> childOpt : children.values()) {
				childOpt.queryForEach(sx, sy, fx, fy, consumer);
			}
		}
	}
	
	private void query(// NOPMD: False positive
			final int sx,
			final int sy,
			final int fx,
			final int fy,
			final List<E> results
			) {
		assert !(bounds == null && !children.isEmpty());
		if (bounds == null || bounds.intersects(sx, sy, fx, fy)) {
			for (final QuadTreeEntry<E> entry : elements) {
				if (entry.isIn(sx, sy, fx, fy)) {
					results.add(entry.element);
				}
			}
			// If there are no children, this will skip them.
			for (final StreamIntQuadTree<E> childOpt : children.values()) {
				childOpt.query(sx, sy, fx, fy, results);
			}
		}
	}

	private class ItNode<E> {
		ItNode<E> parent;
		StreamIntQuadTree<E> currentTree;
		Iterator<QuadTreeEntry<E>> selfIt;
		Iterator<StreamIntQuadTree<E>> childrenIt;

		ItNode(ItNode<E> parent, StreamIntQuadTree<E> tree) {
			this.parent = parent;
			this.currentTree = tree;
			selfIt = tree.elements.iterator();
			childrenIt = tree.children.values().iterator();
		}

		private QuadTreeEntry<E> entry;

		private boolean hasNext() {
			return selfIt.hasNext();
		}

		private E next() {
			entry = selfIt.next();
			return entry.element;
		}

	}

	private class QuadIterator<E> implements Iterator<E> {

		ItNode<E> currentNode;

		QuadIterator(StreamIntQuadTree<E> root) {
			ItNode<E> node = new ItNode<>(null, root);
			currentNode = node;
		}

		private E current;

		@Override
		public boolean hasNext() {
			if(currentNode.hasNext()) {
				return true;
			} else if(currentNode.childrenIt.hasNext()) {
				currentNode = new ItNode(currentNode, currentNode.childrenIt.next());
				return currentNode.hasNext();
			}
			if(currentNode.parent != null) {
				currentNode = new ItNode(currentNode, currentNode.childrenIt.next());
				return currentNode.hasNext();
			}
			if(currentNode.parent == null) {
				return false;
			}
			return currentNode.parent.hasNext();
		}

		@Override
		public E next() {
			if(current == null) {
				if(!hasNext()) throw new NoSuchElementException();
			}
			if(currentNode.selfIt.hasNext()) {
				return currentNode.selfIt.next().element;
			} else if(currentNode.childrenIt.hasNext()) {
				currentNode = new ItNode(currentNode, currentNode.childrenIt.next());
				E e = currentNode.next();
				if(e != null) {
					return e;
				}
			}
			if(currentNode.parent != null) {
				currentNode = new ItNode(currentNode.parent, currentNode.childrenIt.next());
				return currentNode.next();
			}
			throw new NoSuchElementException("End of QuadTree Iterator");
		}

	}

	public Stream<E> queryStreamIter() {
		QuadIterator<E> it = new  QuadIterator<>(this.root);
		Iterable<E> iterable = () -> it;
		return StreamSupport.stream(iterable.spliterator(), false);
	}

	//	private void queryStream(// NOPMD: False positive
	//			final double sx,
	//			final double sy,
	//			final double fx,
	//			final double fy,
	//			final Iterator<E> it
	//			) {
	//		assert !(bounds == null && !children.isEmpty());
	//		if (bounds == null || bounds.intersects(sx, sy, fx, fy)) {
	//			for (final QuadTreeEntry<E> entry : elements) {
	//				if (entry.isIn(sx, sy, fx, fy)) {
	//					results.add(entry.element);
	//				}
	//			}
	//			// If there are no children, this will skip them.
	//			for (final StreamQueadTree<E> childOpt : children.values()) {
	//				childOpt.query(sx, sy, fx, fy, results);
	//			}
	//		}
	//	}

	@Override
	public List<E> query(final int[]... space) {
		if (space.length != 2 || space[0].length != 2 || space[1].length != 2) {
			throw new IllegalArgumentException();
		}
		return query(space[0][0], space[0][1], space[1][0], space[1][1]);
	}

	public Stream<E> tiles() {
		return allTrees()
				.flatMap(StreamIntQuadTree::selfTiles);
	}

	private Stream<E> selfTiles() {
		return selfEntries()
				.map(QuadTreeEntry::element);
	}

	private final Stream<QuadTreeEntry<E>> selfEntries() {
		return elements.stream();
	}

	private final Stream<StreamIntQuadTree<E>> childTrees() {
		return Stream.concat(Stream.of(this), children.values().stream()
				.flatMap(StreamIntQuadTree::childTrees));
	}

	protected Stream<StreamIntQuadTree<E>> allTrees() {
		return findRoot().childTrees();
	}

	protected StreamIntQuadTree<E> findRoot() {
		StreamIntQuadTree<E> r = root;
		if(root == null) return this;
		StreamIntQuadTree<E> newRoot;
		while((newRoot = r.parent) != null) {
			r = newRoot;
		}
		return r;
	}

	//	public Stream<E> queryStream(final double[]... space) {
	//		if (space.length != 2 || space[0].length != 2 || space[1].length != 2) {
	//			throw new IllegalArgumentException();
	//		}
	//		return queryStream(space[0][0], space[0][1], space[1][0], space[1][1]);
	//	}
	//	
	//	public Stream<E> queryStream(double ax, double ay, double bx, double by) {
	//		QuadIterator<E> it = new  QuadIterator<>(this.root);
	//		Iterable<E> iterable = () -> it;
	//		return StreamSupport.stream(iterable.spliterator(), false);
	//	}

	@Override
	public boolean remove(final E e, final int... pos) {
		assert pos.length == 2;
		return remove(e, pos[0], pos[1]);
	}

	/**
	 * Same of {@link #remove(Object, double...)} with explicit parameters.
	 *
	 * @param e Element to remove
	 * @param x X position of the element
	 * @param y Y position of the element
	 *
	 * @return true if the element has been found and removed
	 */
	public boolean remove(final E e, final int x, final int y) {
		return root.removeHere(e, x, y);
	}

	private boolean removeHere(final E e, final int x, final int y) {
		if (contains(x, y)) {
			return elements.remove(new QuadTreeEntry<>(e, x, y)) || removeInChildren(e, x, y);
		}
		return false;
	}

	private boolean removeInChildren(final E e, final int x, final int y) {
		return children.values().stream().anyMatch(c -> c.removeHere(e, x, y));
	}

	private StreamIntQuadTree<E> selectChild(final int x, final int y) {
		assert !children.isEmpty();
		if (x < centerX()) {
			return y < centerY() ? children.get(Child.BL) : children.get(Child.TL);
		} else {
			return y < centerY() ? children.get(Child.BR) : children.get(Child.TR);
		}
	}

	private void setChild(final Child c, final StreamIntQuadTree<E> child) {
		if (children.put(c, child) != null) {
			throw new IllegalStateException();
		}
		child.parent = this;
	}

	private void subdivide() {
		for (final Child c : Child.values()) {
			createChildIfAbsent(c);
		}
	}

	private boolean swapMostStatic(final E e, final int fx, final int fy) {
		assert parent != null : "Tried to swap on a null parent.";
		final Iterator<QuadTreeEntry<E>> iterator = parent.elements.descendingIterator();
		while (iterator.hasNext()) {
			final QuadTreeEntry<E> target = iterator.next();
			if (contains(target.x, target.y)) {
				/*
				 * There is a swappable node
				 */
				iterator.remove();
				elements.push(target);
				parent.insertNode(e, fx, fy);
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return (bounds == null ? "Unbounded" : bounds.toString()) + ":" + elements.toString();
	}

	private enum Child {
		TR, BR, BL, TL
	}

	private static class QuadTreeEntry<E> implements Serializable {
		private static final long serialVersionUID = 9021533648086596986L;
		private static final HashFunction HASHER = Hashing.murmur3_32(0);
		
		
		private final E element;
		private final int x, y;

		QuadTreeEntry(final E el, final int xp, final int yp) {
			element = el;
			x = xp;
			y = yp;
		}

		protected final E element() {
			return element;
		}

		@Override
		public boolean equals(final Object obj) {
			if (obj == this) {
				return true;
			}
			if (obj instanceof QuadTreeEntry<?>) {
				final QuadTreeEntry<?> e = (QuadTreeEntry<?>) obj;
				if (samePosition(e)) {
					// NOPMD: Pointer comparison intended
					return Objects.equals(element, e.element);
				}
				return false;
			}
			return false;
		}

		@Override
		@SuppressWarnings("UnstableApiUsage")
		public int hashCode() {
			return HASHER.newHasher()
					.putInt(x)
					.putInt(y)
					.putInt(element.hashCode())
					.hash()
					.asInt();
		}

		public boolean isIn(final double sx, final double sy, final double fx, final double fy) {
			return x >= sx && x < fx && y >= sy && y < fy;
		}

		public boolean samePosition(final QuadTreeEntry<?> target) {
			return x == target.x && y == target.y;
		}

		@Override
		public String toString() {
			return element.toString() + "@[" + x + ", " + y + "]";
		}
	}

	private static class Rectangle2DInt implements Serializable {
		private static final long serialVersionUID = -7890062202005580979L;
		private final int minx, miny, maxx, maxy;

		Rectangle2DInt(final int sx, final int sy, final int fx, final int fy) {
			minx = min(sx, fx);
			miny = min(sy, fy);
			maxx = max(sx, fx);
			maxy = max(sy, fy);
		}

		public boolean contains(final int x, final int y) {
			return x >= minx && y >= miny && x < maxx && y < maxy;
		}

		public int getCenterX() {
			return minx + (maxx - minx) / 2;
		}

		public int getCenterY() {
			return miny + (maxy - miny) / 2;
		}

		public int getMaxX() {
			return maxx;
		}

		public int getMaxY() {
			return maxy;
		}

		public int getMinX() {
			return minx;
		}

		public int getMinY() {
			return miny;
		}

		public boolean intersects(final int sx, final int sy, final int fx, final int fy) {
			return fx >= minx && fy >= miny && sx < maxx && sy < maxy;
		}

		@Override
		public String toString() {
			return "[" + minx + "," + miny + " - " + maxx + "," + maxy + "]";
		}
	}

}
