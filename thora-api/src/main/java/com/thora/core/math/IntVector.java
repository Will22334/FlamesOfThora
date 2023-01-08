package com.thora.core.math;

public interface IntVector extends Vector {

	@Override
	int getIX();

	@Override
	int getIY();

	@Override
	IntVector setX(int x);

	@Override
	IntVector setY(int y);

	@Override
	default double getX() {
		return getIX();
	}

	@Override
	default double getY() {
		return getIY();
	}

	@Override
	default IntVector setX(double x) {
		return setX((int)x);
	}

	@Override
	default IntVector setY(double y) {
		return setY((int)y);
	}

	@Override
	default IntVector setAs(int x, int y) {
		return setX(x).setY(y);
	}

	@Override
	default IntVector setAs(double x, double y) {
		return setAs((int)x, (int)y);
	}
	
	@Override
	default IntVector setAs(Vector v) {
		return setAs(v.getIX(), v.getIY());
	}
	
	@Override
	default IntVector add(double dx, double dy) {
		return add((int)dx, (int)dy);
	}

	@Override
	default IntVector add(int dx, int dy) {
		return setAs(getIX() + dx, getIY() + dy);
	}

	@Override
	default IntVector add(Vector v) {
		return add(v.getIX(), v.getIY());
	}

	@Override
	default IntVector subtract(double dx, double dy) {
		return add((int)-dx, (int)-dy);
	}

	@Override
	default IntVector subtract(int dx, int dy) {
		return add(-dx, -dy);
	}

	@Override
	default IntVector subtract(Vector v) {
		return subtract(v.getIX(), v.getIY());
	}
	
	public default int[] comps() {
		return new int[] {getIX(), getIY()};
	}
	
	@Override
	default boolean isInteger() {
		return true;
	}

	@Override
	IntVector clone();

}
