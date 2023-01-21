package com.thora.core.math;

import java.util.Objects;

public class BasicDoubleVector implements DoubleVector {
	
	private double x, y;
	
	public BasicDoubleVector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public BasicDoubleVector(Vector v) {
		this(v.getX(), v.getY());
	}
	
	public BasicDoubleVector() {
		this(0d, 0d);
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
	public BasicDoubleVector setX(double x) {
		this.x = x;
		return this;
	}

	@Override
	public BasicDoubleVector setY(double y) {
		this.y = y;
		return this;
	}

	@Override
	public BasicDoubleVector setX(int x) {
		return setX((double)x);
	}

	@Override
	public BasicDoubleVector setY(int y) {
		return setY((double)y);
	}
	
	@Override
	public BasicDoubleVector clone() {
		return new BasicDoubleVector(this);
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Vector) {
			Vector o = (Vector) obj;
			return x == o.getX() && y == o.getY();
		}
		return false;
	}

	@Override
	public String toString() {
		return "[" + x + ", " + y + "]";
	}
	
}
