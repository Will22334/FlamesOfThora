package com.thora.core.world;

import java.util.stream.Stream;

public abstract class Chunk implements HasWorld, RectangularRegion {
	
	@Override
	public abstract World getWorld();
	
	public abstract Location getOrigin();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getOrigin() + "[" + getWidth() + "x" + getHeight() + "]";
	}
	
	@Override
	public Stream<? extends Tile> tiles() {
		final Location o = getOrigin();
		return getWorld().tiles(o.getX(), o.getY(), o.getX() + getWidth(), o.getY() + getHeight());
	}
	
	@Override
	public WorldRectangle getRectRegion() {
		return RectangularRegion.computeRect(getOrigin(), getWidth(), getHeight());
	}
	
}
