package com.thora.core.world;

public class Location implements Cloneable {
	
	private int x;
	private int y;
	
	public Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public final int getX() {
		return x;
	}
	
	public final int getY() {
		return y;
	}
	
	public Location getLocation() {
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
	protected Location clone() throws CloneNotSupportedException {
		return new Location(getX(), getY());
	}
	
}
