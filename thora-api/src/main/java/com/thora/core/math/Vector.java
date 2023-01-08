package com.thora.core.math;

import java.util.Objects;

public interface Vector extends Cloneable {

	public static final double PI = Math.PI;
	public static final double PI_2 = Math.PI / 2d;
	public static final double PI2 = 2 * Math.PI;
	
	public static final double LEFT_90 = Math.toRadians(-90d);
	public static final double RIGHT_90 = Math.toRadians(90d);
	
	public static final double DIRECTION_UP = Math.PI/2,
			DIRECTION_LEFT = PI,
			DIRECTION_DOWN = -Math.PI/2,
			DIRECTION_RIGHT = 0d;
	
	public static double polishAngle(double a) {
		a %= PI2;
		if(a < 0)
			a += PI2;
		return a;
	}
	
	public static Vector unmodifiable(Vector v, boolean failFast) {
		Objects.requireNonNull(v, "Cannot make a null Vector immutable");
		if(failFast) {
			if(v instanceof FailFinalVector) {
				return (FailFinalVector) v;
			}
			return new FailFinalVector(v);
		} else {
			if(v instanceof CloneFinalVector) {
				return (CloneFinalVector)v;
			}
			return new CloneFinalVector(v);
		}
	}
	
	public static Vector unmodifiable(Vector v) {
		return unmodifiable(v, false);
	}
	
	/**
	 * Gets the X value of this Vector.
	 * @return X value
	 */
	public double getX();

	/**
	 * Gets the Y value of this Vector.
	 * @return Y value
	 */
	public double getY();

	/**
	 * Gets the rounded integer X value of this Vector.
	 * @return Rounded X value
	 */
	public default int getIX() {
		return (int) Math.round(getX());
	}

	/**
	 * Gets the rounded integer Y value of this Vector.
	 * @return Rounded Y value
	 */
	public default int getIY() {
		return (int) Math.round(getY());
	}

	/**
	 * Sets the X value of this Vector.
	 * @param x the new X value
	 * @return This Vector for chaining
	 */
	public Vector setX(double x);

	/**
	 * Sets the Y value of this Vector.
	 * @param x the new Y value
	 * @return This Vector for chaining
	 */
	public Vector setY(double y);

	/**
	 * Sets the X value of this Vector.
	 * @param x the new X value
	 * @return This Vector for chaining
	 */
	public default Vector setX(int x) {
		return setX((double)x);
	}

	/**
	 * Sets the Y value of this Vector.
	 * @param x the new Y value
	 * @return This Vector for chaining
	 */
	public default Vector setY(int y) {
		return setY((double)y);
	}
	
	/**
	 * Returns the length of this vector.
	 * @return The vectors length
	 */
	public default double getLength() {
		return Math.hypot(getX(), getY());
	}
	
	/**
	 * Returns the angle this Vector is facing relative to the origin.
	 * The returned angle is in range of <i>(-Pi,Pi)<i>
	 * @return The angle(arcTangent) in radians
	 */
	public default double getAngle() {
		return Math.atan2(getY(), getX());
	}

	/**
	 * Scales this Vector by the passed ratio.
	 * If this vector is == 0 then the resulting length and angle will be preserved.
	 * @param s The ratio to scale this vector by
	 * @return This Vector for chaining
	 */
	public default Vector scale(double s) {
		return setAs(s * getX(), s * getY());
	}

	public default Vector setLength(double r) {
		return scale(r / getLength());
	}

	public default Vector setAngle(double a) {
		return setAsP(getLength(), a);
	}

	public default Vector rotate(double da) {
		return setAngle(getAngle() + da);
	}

	/**
	 * Set this Vector using cartesian(x,y) coordinates.
	 * @param x
	 * @param y
	 * @return
	 */
	public default Vector setAs(double x, double y) {
		return setX(x).setY(y);
	}

	/**
	 * Set this vector using polar(r,a) coordinates.
	 * @param r
	 * @param a
	 * @return
	 */
	public default Vector setAsP(double r, double a) {
		a = Vector.polishAngle(a);
		return setAs(r * Math.cos(a), r * Math.sin(a));
	}

	/**
	 * Set this Vector using cartesian(x,y) grid coordinates.
	 * @param x
	 * @param y
	 * @return
	 */
	public default Vector setAs(int x, int y) {
		return setAs((double)x, (double)y);
	}

	/**
	 * Set this Vector to the value of the passed Vector.
	 * This is logically equivalent to {@link clone} but
	 * may not use the same coordinate system.
	 * @param v The Vector values to use
	 * @return This Vecvtor after being altered for chaining.
	 */
	public default Vector setAs(Vector v) {
		return setAs(v.getX(), v.getY());
	}

	/**
	 * Sets this Vector's length to 0.
	 * @return This now empty Vector for chaining
	 */
	public default Vector clear() {
		return setAs(0, 0);
	}
	
	public default Vector add(double dx, double dy) {
		return setAs(getX() + dx, getY() + dy);
	}
	
	/**
	 * Adds the given values(dx,dy) to this Vector.
	 * @param dx the change in x
	 * @param dy the change in y
	 * @return This Vector for chaining
	 */
	public default Vector add(int dx, int dy) {
		return setAs(getIX() + dx, getIY() + dy);
	}
	
	/**
	 * Adds the passed Vector to this one without altering the operand.
	 * 
	 * @param v Vector that will add onto this
	 * @return The calling Vector after addition for chaining.
	 */
	public default Vector add(Vector v) {
		return setAs(getX() + v.getX(), getY() + v.getY());
	}
	
	public default Vector subtract(double dx, double dy) {
		return add(-dx, -dy);
	}
	
	public default Vector subtract(int dx, int dy) {
		return add(-dx, -dy);
	}
	
	/**
	 * Subtracts the passed Vector to this one without altering the operand.
	 * This is mathematically equivalent to adding the negation of the argument.
	 * 
	 * @param v Vector that will add onto this
	 * @return The calling Vector after subtraction for chaining.
	 */
	public default Vector subtract(Vector v) {
		return setAs(getX() - v.getX(), getY() - v.getY());
	}
	
	/**
	 * Turn this vector into it's opposite.
	 * This can be viewed as either fliping it through the Z-axis in 3d
	 * or simply rotating the Vector 180 degrees.
	 * 
	 * @return This Vector after being flipped.
	 * 
	 * @note If this Vector's length is == 0 then the behavior of the resulting angle is undetermined.
	 */
	public default Vector negate() {
		return scale(-1d);
	}
	
	public default Vector putComps(double[] arr, int index) {
		arr[index] = getX();
		arr[index + 1] = getY();
		return this;
	}

	public default Vector putComps(double[] arr) {
		return putComps(arr, 0);
	}

	public default Vector putComps(int[] arr, int index) {
		arr[index] = getIX();
		arr[index + 1] = getIY();
		return this;
	}

	public default Vector putComps(int[] arr) {
		return putComps(arr, 0);
	}

	public default Vector putCompsP(double[] arr, int index) {
		arr[index] = getLength();
		arr[index + 1] = getAngle();
		return this;
	}

	public default Vector putCompsP(double[] arr) {
		return putCompsP(arr, 0);
	}

	public default boolean isZero() {
		return getLength() == 0;
	}
	
	public default boolean isMutable() {
		return true;
	}
	
	public boolean isInteger();
	
	public Vector clone();

}
