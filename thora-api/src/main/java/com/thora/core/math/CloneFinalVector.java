package com.thora.core.math;

public class CloneFinalVector implements Vector {

	private final Vector v;

	protected CloneFinalVector(Vector v) {
		this.v = v;
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
		return clone().setX(x);
	}

	@Override
	public Vector setY(double y) {
		return clone().setY(y);
	}

	@Override
	public Vector setX(int x) {
		return clone().setX(x);
	}

	@Override
	public Vector setY(int y) {
		return clone().setY(y);
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
		return clone().scale(s);
	}

	@Override
	public Vector setLength(double r) {
		return clone().setLength(r);
	}

	@Override
	public Vector setAngle(double a) {
		return clone().setAngle(a);
	}

	@Override
	public Vector rotate(double da) {
		return clone().rotate(da);
	}

	@Override
	public Vector setAs(double x, double y) {
		return clone().setAs(x, y);
	}

	@Override
	public Vector setAsP(double r, double a) {
		return clone().setAsP(r, a);
	}

	@Override
	public Vector setAs(int x, int y) {
		return clone().setAs(x, y);
	}

	@Override
	public Vector setAs(Vector v) {
		return clone().setAs(v);
	}

	@Override
	public Vector clear() {
		return clone().clear();
	}

	@Override
	public Vector add(double dx, double dy) {
		return clone().add(dx, dy);
	}

	@Override
	public Vector add(int dx, int dy) {
		return clone().add(dx, dy);
	}

	@Override
	public Vector add(Vector v) {
		return clone().add(v);
	}

	@Override
	public Vector subtract(double dx, double dy) {
		return clone().subtract(dx, dy);
	}

	@Override
	public Vector subtract(int dx, int dy) {
		return clone().subtract(dx, dy);
	}

	@Override
	public Vector subtract(Vector v) {
		return clone().subtract(v);
	}

	@Override
	public Vector negate() {
		return clone().negate();
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
	public boolean isMutable() {
		return v.isMutable();
	}

	@Override
	public boolean isInteger() {
		return v.isInteger();
	}

	@Override
	public Vector clone() {
		return v.clone();
	}

}
