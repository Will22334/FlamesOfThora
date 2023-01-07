package com.thora.core.math;

public interface DoubleVector extends Vector {

	DoubleVector setX(double x);

	DoubleVector setY(double y);

	@Override
	default boolean isInteger() {
		return false;
	}

	DoubleVector clone();
	
}
