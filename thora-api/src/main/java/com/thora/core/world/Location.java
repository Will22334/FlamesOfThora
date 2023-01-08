package com.thora.core.world;

import com.thora.core.math.IntVector;
import com.thora.core.math.Vector;

public abstract class Location implements Locatable, Cloneable {
	
	public Location() {
		
	}
	
	@Override
	public abstract World getWorld();
	
	/**
	 * Returns a {@link Vector} that represents the position of this {@link Location}
	 * in a particular {@link World} relative the worlds origin.
	 * @return Vector representation of this Location
	 */
	public abstract Vector asVector();
	
	@Override
	public final int getX() {
		return asVector().getIX();
	}
	
	@Override
	public final int getY() {
		return asVector().getIY();
	}
	
	public abstract Location setAs(int x, int y);
	
	public double[] comps() {
		return new double[] {asVector().getX(), asVector().getY()};
	}
	
	public Location comps(double[] arr, int index) {
		arr[index] = getX();
		arr[index + 1] = getY();
		return this;
	}
	
	public Location comps(double[] arr) {
		return comps(arr, 0);
	}
	
	@Override
	public Location getLocation() {
		return this;
	}
	
	public final int getOrthogonallDistance(Location p) {
		return Math.abs((p.getX() - getX())) + Math.abs((p.getY() - getY()));
	}
	
	public final double getDistance(Location p) {
		return Math.hypot(p.getX() - getX(), p.getY() - getY());
	}
	
	@Override
	public final double getDistance(Locatable loc) {
		return getDistance(loc.getLocation());
	}
	
	public final Vector getDifference(Location p, Vector result) {
		return result.setAs(p.asVector()).subtract(asVector());
	}
	
	public final Vector getDifference(Location p) {
		return p.asVector().clone().subtract(asVector());
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
		return shift(v.getIX(), v.getIY());
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
