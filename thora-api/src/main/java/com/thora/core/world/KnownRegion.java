package com.thora.core.world;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link Region} that has a finite amount of {@link Location points} at any given instant.
 * The region does not guarantee continuity.
 * 
 * @author Dave
 *
 */
public interface KnownRegion extends Region {
	
	/**
	 * Returns a {@link Collection} that contains every point inside this Region.
	 * The Collection does not contain duplicate nor null elements.
	 * The ordering of the Collection is undefined.
	 * @return a Collection containing all points inside this Region.
	 */
	public default Collection<Location> getPoints() {
		return points()
				.collect(Collectors.toSet());
	}
	
	/**
	 * Returns a {@link Stream} containing every point inside this {@link Region}.
	 * The stream contains unique non-null elements with an undefined ordering.
	 * @return a stream of all points this Region contains.
	 */
	public Stream<Location> points();
	
	public default Stream<? extends Tile> tiles() {
		return points()
				.map(Location::getTile);
	}
	
}
