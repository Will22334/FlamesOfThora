package com.thora.core.world;

import com.thora.core.math.IntVector;
import com.thora.core.math.Vector;

public abstract class IntVectorLocation extends Location {
	
	protected IntVector v;
	
	public IntVectorLocation(int x, int y) {
		this(new IntVector(x, y));
	}
	
	public IntVectorLocation(IntVector v) {
		this.v = v;
	}
	
	public abstract World getWorld();
	
	public IntVector vector() {
		return v;
	}
	
	@Override
	public final int getX() {
		return vector().getIX();
	}
	
	@Override
	public final int getY() {
		return vector().getIY();
	}
	
	@Override
	public IntVectorLocation setAs(int x, int y) {
		this.v.setAs(x, y);
		return this;
	}
	
	public IntVectorLocation setAs(IntVector v) {
		this.v.setAs(v);
		return this;
	}
	
	@Override
	public IntVectorLocation getLocation() {
		return this;
	}
	
	/**
	 * Shifts this location by given vector.
	 * @param v
	 * @return This Location for chaining.
	 */
	public IntVectorLocation shift(IntVector v) {
		return shift(v.getIX(), v.getIY());
	}
	
	@Override
	public IntVectorLocation shift(int dx, int dy) {
		return this.setAs(getX() + dx, getY() + dy);
	}
	
	
	
	@Override
	public Vector<IntVector> asVector() {
		return vector();
	}

	@Override
	public double[] comps() {
		return new double[] {getX(), getY()};
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
	public abstract IntVectorLocation clone();
	
}
