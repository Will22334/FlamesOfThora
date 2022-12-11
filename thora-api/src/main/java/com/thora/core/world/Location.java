package com.thora.core.world;

import com.thora.core.math.IntVector;

public abstract class Location implements Locatable, Cloneable {
	
	public Location() {
		
	}
	
	public abstract World getWorld();
	
	@Override
	public abstract int getX();
	
	@Override
	public abstract int getY();
	
	public abstract Location setAs(int x, int y);
	
	@Override
	public Location getLocation() {
		return this;
	}
	
	public final int getTileDistance(Location p) {
		return Math.max(Math.abs((p.getX() - getX())), Math.abs((p.getY() - getY())));
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
		return setAs(getX() + dx, getY() + dy);
	}
	
	/**
	 * Shifts this location by given vector.
	 * @param v
	 * @return This Location for chaining.
	 */
	public Location shift(IntVector v) {
		return shift(v.x, v.y);
	}
	
	@Override
	public String toString() {
		return "[" + getX() + "," + getY() + "]";
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
	public abstract Location clone();
	
}
