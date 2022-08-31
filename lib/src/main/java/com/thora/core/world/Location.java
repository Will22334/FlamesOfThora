package com.thora.core.world;

public class Location implements Locatable, Cloneable {
	
	private int x;
	private int y;
	
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	@Override
	public final int getX() {
		return x;
	}
	
	@Override
	public final int getY() {
		return y;
	}
	
	@Override
	public final Location getLocation() {
		return this;
	}
	
	public final double getDistance(Location p) {
		return Math.hypot(p.getX() - getX(), p.getY() - getY());
	}
	
	@Override
	public final double getDistance(Locatable loc) {
		return getDistance(loc.getLocation());
	}
	
	/**
	 * Shifts this location by given dx and dy.
	 * @param dx
	 * @param dy
	 * @return This Location for chaining.
	 */
	public Location shift(int dx, int dy) {
		x += dx;
		y += dy;
		return this;
	}
	
	@Override
	public String toString() {
		return "[" + getX() + ", " + getY() + "]";
	}
	
	@Override
	public int hashCode() {
		return 31 * (31 + getX()) + getY();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Location) {
			Location l = (Location) obj;
			return getX() == l.getX() &&
					getY() == l.getY();
		}
		return false;
	}
	
	@Override
	protected Location clone() {
		return new Location(getX(), getY());
	}
	
}
