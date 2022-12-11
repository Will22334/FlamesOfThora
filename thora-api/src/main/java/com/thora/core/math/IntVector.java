package com.thora.core.math;

public class IntVector {
	
	protected int x, y;
	
	public IntVector(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY(int y) {
		this.y = y;
	}
	
	public final int maxRectLength() {
		return Math.max(Math.abs(x), Math.abs(y));
	}
	
}
