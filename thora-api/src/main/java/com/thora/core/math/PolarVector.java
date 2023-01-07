package com.thora.core.math;

public class PolarVector implements Vector {

	private double r, a;

	public PolarVector(double r, double a) {
		this.r = r;
		this.a = a;
	}

	public PolarVector(Vector v) {
		this(v.getLength(), v.getAngle());
	}

	@Override
	public double getLength() {
		return r;
	}

	@Override
	public double getAngle() {
		return a;
	}

	@Override
	public double getX() {
		return getLength() * Math.cos(getAngle());
	}

	@Override
	public double getY() {
		return getLength() * Math.sin(getAngle());
	}

	@Override
	public PolarVector setX(double x) {
		return setAs(x, getY());
	}

	@Override
	public PolarVector setY(double y) {
		return setAs(getX(), y);
	}

	@Override
	public PolarVector setAs(double x, double y) {
		this.r = Math.hypot(x, y);
		this.a = Math.atan2(y, x);
		return this;
	}

	@Override
	public PolarVector scale(double s) {
		r *= s;
		return this;
	}

	@Override
	public PolarVector setAngle(double a) {
		this.a = a;
		return this;
	}

	@Override
	public PolarVector rotate(double da) {
		this.a = Vector.polishAngle(a + da);
		return this;
	}

	@Override
	public PolarVector setAsP(double r, double a) {
		this.r = r;
		this.a = a;
		return this;
	}
	
	@Override
	public PolarVector setLength(double r) {
		this.r = r;
		return this;
	}

	@Override
	public PolarVector clear() {
		this.r = 0;
		return this;
	}

	@Override
	public PolarVector negate() {
		return rotate(Math.PI);
	}

	@Override
	public boolean isZero() {
		return r == 0d;
	}
	
	@Override
	public PolarVector clone() {
		return new PolarVector(getLength(), getAngle());
	}

	@Override
	public boolean isInteger() {
		return false;
	}

}
