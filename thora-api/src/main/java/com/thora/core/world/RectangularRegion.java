package com.thora.core.world;

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A {@link KnownRegion} that has a defined rectangular shape.
 * The bounds of the region are supplied by {@link #getRectRegion()}.
 * 
 * @author Dave
 *
 */
public interface RectangularRegion extends KnownRegion {
	
	public static WorldRectangle computeRect(Locatable origin, int width, int height) {
		return new WorldRectangle(origin.getLocation(), width, height);
	}
	
	public WorldRectangle getRectRegion();
	
	public default long getSurfaceArea() {
		WorldRectangle r = getRectRegion();
		return (long)(r.getWidth() * r.getHeight());
	}
	
	@Override
	public default boolean contains(Locatable loc) {
		if(loc == null) return false;
		if(!getRectRegion().getWorld().equals(loc.getWorld())) {
			return false;
		}
		return getRectRegion().contains(loc.getX(), loc.getY());
	}
	
	@Override
	default Stream<Location> points() {
		final WorldRectangle r = getRectRegion();
		return IntStream.rangeClosed(r.getMinY(), r.getMaxY())
				.mapToObj(y -> {
					return IntStream.rangeClosed(r.getMinX(), r.getMaxX())
							.mapToObj(x -> new WeakVectorLocation<>(x, y));
				})
				.flatMap(Function.identity());
		
	}
	
}
