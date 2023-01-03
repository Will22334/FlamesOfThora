package com.thora.core.math;

public class DoubleVector implements Vector<DoubleVector> {
	
	private double x, y;
	
	public DoubleVector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public DoubleVector(Vector<?> v) {
		this(v.getX(), v.getY());
	}
	
	public DoubleVector() {
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
	public int getIX() {
		return (int)Math.ceil(x);
	}

	@Override
	public int getIY() {
		return (int)Math.ceil(y);
	}

	@Override
	public DoubleVector setX(double x) {
		this.x = x;
		return this;
	}

	@Override
	public DoubleVector setY(double y) {
		this.y = y;
		return this;
	}

	@Override
	public DoubleVector setX(int x) {
		return setX((double)x);
	}

	@Override
	public DoubleVector setY(int y) {
		return setY((double)y);
	}
	
	@Override
	public DoubleVector clone() {
		return new DoubleVector(this);
	}
	
}
