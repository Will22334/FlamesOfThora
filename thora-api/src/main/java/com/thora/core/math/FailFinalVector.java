package com.thora.core.math;

public final class FailFinalVector implements Vector {

	private final Vector v;

	protected FailFinalVector(Vector v) {
		this.v = v;
	}
	
	protected Vector vector() {
		return v;
	}
	
	protected FailFinalVector fail() {
		throw new UnsupportedOperationException("Cannot modify UnmodifiableVector");
	}
	
	@Override
	public double getX() {
		return v.getX();
	}

	@Override
	public double getY() {
		return v.getY();
	}

	@Override
	public int getIX() {
		return v.getIX();
	}

	@Override
	public int getIY() {
		return v.getIY();
	}

	@Override
	public Vector setX(double x) {
		return fail();
	}

	@Override
	public Vector setY(double y) {
		return fail();
	}

	@Override
	public Vector setX(int x) {
		return fail();
	}

	@Override
	public Vector setY(int y) {
		return fail();
	}

	@Override
	public double getLength() {
		return v.getLength();
	}

	@Override
	public double getAngle() {
		return v.getAngle();
	}

	@Override
	public Vector scale(double s) {
		return fail();
	}

	@Override
	public Vector setLength(double r) {
		return fail();
	}

	@Override
	public Vector setAngle(double a) {
		return fail();
	}

	@Override
	public Vector rotate(double da) {
		return fail();
	}

	@Override
	public Vector setAs(double x, double y) {
		return fail();
	}

	@Override
	public Vector setAsP(double r, double a) {
		return fail();
	}

	@Override
	public Vector setAs(int x, int y) {
		return fail();
	}

	@Override
	public Vector setAs(Vector v) {
		return fail();
	}

	@Override
	public Vector clear() {
		return fail();
	}

	@Override
	public Vector add(double dx, double dy) {
		return fail();
	}

	@Override
	public Vector add(int dx, int dy) {
		return fail();
	}

	@Override
	public Vector add(Vector v) {
		return fail();
	}

	@Override
	public Vector subtract(double dx, double dy) {
		return fail();
	}

	@Override
	public Vector subtract(int dx, int dy) {
		return fail();
	}

	@Override
	public Vector subtract(Vector v) {
		return fail();
	}

	@Override
	public Vector negate() {
		return fail();
	}

	@Override
	public Vector putComps(double[] arr, int index) {
		return v.putComps(arr, index);
	}

	@Override
	public Vector putComps(double[] arr) {
		return v.putComps(arr);
	}

	@Override
	public Vector putComps(int[] arr, int index) {
		return v.putComps(arr, index);
	}

	@Override
	public Vector putComps(int[] arr) {
		return v.putComps(arr);
	}

	@Override
	public Vector putCompsP(double[] arr, int index) {
		return v.putCompsP(arr, index);
	}

	@Override
	public Vector putCompsP(double[] arr) {
		return v.putCompsP(arr);
	}

	@Override
	public boolean isZero() {
		return v.isZero();
	}

	@Override
	public Vector clone(){
		return vector().clone();
	}

	@Override
	public boolean isInteger() {
		return v.isInteger();
	}

}
