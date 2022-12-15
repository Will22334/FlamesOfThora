package com.thora.core.math;

public interface Vector<V extends Vector<V>> extends Cloneable {
	
	public double getX();
	public double getY();
	
	public int getIX();
	public int getIY();
	
	public V setX(double x);
	public V setY(double y);
	
	public V setX(int x);
	public V setY(int y);
	
	public V setAs(double x, double y);
	
	public default V setAs(int x, int y) {
		return setAs((double)x, (double)y);
	}
	
	public default Vector<V> putComps(double[] arr, int index) {
		arr[index] = getX();
		arr[index + 1] = getY();
		return this;
	}
	
	public default Vector<V> putComps(double[] arr) {
		return putComps(arr, 0);
	}
	
	public default Vector<V> putComps(int[] arr, int index) {
		arr[index] = getIX();
		arr[index + 1] = getIY();
		return this;
	}
	
	public default Vector<V> putComps(int[] arr) {
		return putComps(arr, 0);
	}
	
	public V clone() throws CloneNotSupportedException;
	
}
