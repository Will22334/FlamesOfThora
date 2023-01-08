package com.thora.core.world;

import java.util.Objects;

public class WorldRectangle implements RectangularRegion {
	
	private Location bottomLeft, topRight;
	
	WorldRectangle(Location bottomLeft, Location topRight) {
		if(!bottomLeft.sameWorld(topRight)) {
			throw new IllegalArgumentException("Cannot create " + getClass().getSimpleName() + " with Locations in different Worlds");
		}
		this.bottomLeft = bottomLeft;
		this.topRight = topRight;
	}
	
	WorldRectangle(Location bottomLeft, int width, int height) {
		this.bottomLeft = bottomLeft;
		this.topRight = bottomLeft.clone().shift(width, height);
	}
	
	public Location getBottomLeft() {
		return bottomLeft;
	}
	
	public Location getTopRight() {
		return topRight;
	}
	
	@Override
	public WorldRectangle getRectRegion() {
		return this;
	}

	@Override
	public World getWorld() {
		return bottomLeft.getWorld();
	}
	
	public int getX() {
		return getMinX();
	}
	
	public int getY() {
		return getMinY();
	}
	
	public int getWidth() {
		return getTopRight().getX() - getBottomLeft().getX();
	}
	
	public int getHeight() {
		return getTopRight().getY() - getBottomLeft().getY();
	}
	
	public int getMinX() {
		return bottomLeft.getX();
	}
	
	public int getMaxX() {
		return topRight.getX();
	}
	
	public int getMinY() {
		return bottomLeft.getY();
	}
	
	public int getMaxY() {
		return topRight.getY();
	}
	
	@Override
	public boolean contains(Locatable loc) {
		Objects.requireNonNull(loc, "WorldRectangle cannot check contains on Location with null World");
		if(!getWorld().equals(loc.getWorld())) {
			return false;
		}
		return contains(loc.getX(), loc.getY());
	}
	
	public boolean contains(int x, int y) {
		return x >= getMinX() && x <= getMaxX()
				&& y >= getMinY() && y <= getMaxY();
	}
	
}
