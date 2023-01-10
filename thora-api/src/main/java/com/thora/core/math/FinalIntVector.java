package com.thora.core.math;

public class FinalIntVector implements IntVector {

	protected final int x, y;
	
	FinalIntVector(int[] comps) {
		this(comps[0], comps[1]);
	}
	
	public FinalIntVector(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public FinalIntVector() {
		this(0, 0);
	}
	
	protected FinalIntVector fail() {
		throw new UnsupportedOperationException("Cannot modify FinalVector");
	}
	
	@Override
	public final int getIX() {
		return x;
	}
	
	@Override
	public final int getIY() {
		return y;
	}
	
	@Override
	public final FinalIntVector setX(int x) {
		return fail();
	}
	
	@Override
	public final FinalIntVector setY(int y) {
		return fail();
	}
	
	@Override
	public final FinalIntVector setAs(int x, int y) {
		return fail();
	}
	
	@Override
	public final FinalIntVector setAs(double x, double y) {
		return setAs((int)x, (int)y);
	}
	
	@Override
	public final FinalIntVector setAs(Vector v) {
		return setAs(v.getIX(), v.getIY());
	}
	
	public final int maxRectLength() {
		return Math.max(Math.abs(x), Math.abs(y));
	}
	
	@Override
	public final boolean isZero() {
		return equals(0, 0);
	}
	
	@Override
	public final FinalIntVector clear() {
		return fail();
	}
	
	@Override
	public final boolean isMutable() {
		return false;
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
	public IntVector clone() {
		return new BasicIntVector(x, y);
	}
	
	@Override
	public String toString() {
		return "F[" + x + ", " + y + "]";
	}

}
