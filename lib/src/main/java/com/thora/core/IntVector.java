package com.thora.core;

public class IntVector {
	
	public int x, y;
	
	public final int maxRectLength() {
		return Math.max(Math.abs(x), Math.abs(y));
	}
	
	public final boolean isZero() {
		return x == 0 && y == 0;
	}
	
	public IntVector clear() {
		x = y = 0;
		return this;
	}
	
}
