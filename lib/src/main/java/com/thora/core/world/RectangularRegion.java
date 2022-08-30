package com.thora.core.world;

import java.awt.Rectangle;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public interface RectangularRegion extends KnownRegion {
	
	public Rectangle getRectRegion();
	
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
							.mapToObj(x -> new Location(x, y));
				})
				.flatMap(Function.identity());
	}
	
}
