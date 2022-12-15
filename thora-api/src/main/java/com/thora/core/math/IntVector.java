package com.thora.core.math;

public class IntVector implements Vector<IntVector> {
	
	protected int x, y;
	
	IntVector(int[] comps) {
		this(comps[0], comps[1]);
	}
	
	public IntVector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public IntVector() {
		this(0, 0);
	}
	
	public int getIX() {
		return x;
	}
	
	public int getIY() {
		return y;
	}
	
	public IntVector setX(int x) {
		this.x = x;
		return this;
	}
	
	public IntVector setY(int x) {
		this.x = x;
		return this;
	}
	
	@Override
	public IntVector setX(double x) {
		this.x = (int) Math.round(x);
		return this;
	}

	@Override
	public IntVector setY(double y) {
		this.y = (int) Math.round(y);
		return this;
	}
	
	public IntVector setAs(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	public IntVector setAs(IntVector v) {
		return setAs(v.getIX(), v.getIY());
	}
	
	public IntVector shift(int dx, int dy) {
		return setAs(getIX() + dx, getIY() + dy);
	}
	
	public final int maxRectLength() {
		return Math.max(Math.abs(x), Math.abs(y));
	}
	
	public final boolean isZero() {
		return equals(0,0);
	}
	
	public IntVector clear() {
		x = y = 0;
		return this;
	}
	
	@Override
	public int hashCode() {
		return 31 * (31 + x) + y;
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(this == o) return true;
		if(o instanceof IntVector) {
			IntVector v = (IntVector) o;
			return x == v.x && y == v.y;
		}
		return false;
	}
	
	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}
	
	@Override
	public IntVector clone() {
		return new IntVector(x, y);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public IntVector setAs(double x, double y) {
		this.x = (int) Math.round(x);
		this.y = (int) Math.round(y);
		return this;
	}
	
}
