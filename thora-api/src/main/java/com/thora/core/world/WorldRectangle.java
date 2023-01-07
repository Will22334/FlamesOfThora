package com.thora.core.world;

public class WorldRectangle {
	
	private Location bottomLeft, topRight;
	
	WorldRectangle(Location bottomLeft, Location topRight) {
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
	
	boolean contains(int x, int y) {
		return x >= getMinX() && x <= getMaxX()
				&& y >= getMinY() && y <= getMaxY();
	}
	
}
