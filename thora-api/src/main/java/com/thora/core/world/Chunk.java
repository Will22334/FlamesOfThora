package com.thora.core.world;

import java.util.stream.Stream;

public abstract class Chunk implements HasWorld, RectangularRegion {
	
	@Override
	public abstract World world();
	
	public abstract Location getOrigin();
	
	public abstract int chunkWidth();
	
	public abstract int chunkHeight();
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + getOrigin() + "[" + chunkWidth() + "x" + chunkHeight() + "]";
	}
	
	@Override
	public Stream<? extends Tile> tiles() {
		final Location o = getOrigin();
		return world().tiles(o.getX(), o.getY(), o.getX() + chunkWidth(), o.getY() + chunkHeight());
	}
	
	@Override
	public WorldRectangle getRectRegion() {
		return RectangularRegion.computeRect(getOrigin(), chunkWidth(), chunkHeight());
	}
	
	public abstract Stream<? extends WorldEntity> entities();
	
}
