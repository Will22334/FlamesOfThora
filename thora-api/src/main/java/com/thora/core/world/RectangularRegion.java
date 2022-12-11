package com.thora.core.world;

import java.awt.Rectangle;
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
	
	public static Rectangle computeRect(Locatable origin, int width, int height) {
		return new Rectangle(origin.getX(), origin.getY(), width, height);
	}
	
	public Rectangle getRectRegion();
	
	public default long getSurfaceArea() {
		Rectangle r = getRectRegion();
		return (long)(r.getWidth() * r.getHeight());
	}
	
	@Override
	public default boolean contains(Locatable loc) {
		if(loc == null) return false;
		return getRectRegion().contains(loc.getX(), loc.getY());
	}

	@Override
	default Stream<Location> points() {
		final Rectangle rect = getRectRegion();
		return IntStream.rangeClosed(rect.y, rect.y + rect.height)
				.mapToObj(y -> {
					return IntStream.rangeClosed(rect.x, rect.x + rect.width)
							.mapToObj(x -> new WeakVectorLocation(x, y));
				})
				.flatMap(Function.identity());
	}
	
}
