package com.thora.core.world;

import java.awt.Rectangle;

public interface RectangularRegion extends KnownRegion {
	
	public Rectangle getRectRegion();
	
	public default boolean contains(Locatable loc) {
		if(loc == null) return false;
		return getRectRegion().contains(loc.getX(), loc.getY());
	}
	
}
