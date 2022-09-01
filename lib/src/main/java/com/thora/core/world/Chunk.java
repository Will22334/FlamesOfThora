package com.thora.core.world;

import java.awt.Rectangle;
import java.util.stream.Stream;

public abstract class Chunk implements RectangularRegion {
	
	public abstract World getWorld();
	
	public abstract Location getOrigin();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public Stream<? extends Tile> tiles() {
		Location o = getOrigin();
		return getWorld().tiles(o.getX(), o.getY(), o.getX() + getWidth(), o.getY() + getHeight());
	}
	
	@Override
	public Rectangle getRectRegion() {
		return RectangularRegion.computeRect(getOrigin(), getWidth(), getHeight());
	}
	
}
