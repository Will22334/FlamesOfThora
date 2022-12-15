package com.thora.core.world;

import java.util.List;

import org.danilopianini.util.SpatialIndex;

public interface IntSpatialIndex<E> {
	
	void insert(E element, int... position);
	
	boolean remove(E element, int... position);
	
	boolean move(E element, int[] start, int[] end);
	
	/**
     * Queries the {@link SpatialIndex}, searching for elements in a
     * parallelotope. "Parallelotope" is a fancy word for
     * "N-dimensional rectangle". A 2-parallelotope is in fact a rectangle, a
     * 3-parallelotope is a parallelepiped, and so on.
     * 
     * @param parallelotope
     *            the space where to search for elements
     * @return the list of elements in this area of the {@link IntSpatialIndex}.
     */
    List<E> query(int[]... parallelotope);
	
	int getDimensions();
	
}
