package com.thora.core.math;

public class BasicIntVector implements IntVector {
	
	protected int x, y;
	
	BasicIntVector(int[] comps) {
		this(comps[0], comps[1]);
	}
	
	public BasicIntVector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public BasicIntVector() {
		this(0, 0);
	}
	
	@Override
	public int getIX() {
		return x;
	}
	
	@Override
	public int getIY() {
		return y;
	}
	
	@Override
	public BasicIntVector setX(int x) {
		this.x = x;
		return this;
	}
	
	@Override
	public BasicIntVector setY(int y) {
		this.y = y;
		return this;
	}
	
	@Override
	public BasicIntVector setAs(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}
	
	@Override
	public BasicIntVector setAs(double x, double y) {
		return setAs((int)x, (int)y);
	}
	
	public BasicIntVector setAs(Vector v) {
		return setAs(v.getIX(), v.getIY());
	}
	
	public final int maxRectLength() {
		return Math.max(Math.abs(x), Math.abs(y));
	}
	
	@Override
	public final boolean isZero() {
		return equals(0,0);
	}
	
	@Override
	public BasicIntVector clear() {
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
			return x == v.getIX() && y == v.getIY();
		}
		return false;
	}
	
	public boolean equals(int x, int y) {
		return this.x == x && this.y == y;
	}
	
	@Override
	public BasicIntVector clone() {
		return new BasicIntVector(x, y);
	}
	
	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
}
