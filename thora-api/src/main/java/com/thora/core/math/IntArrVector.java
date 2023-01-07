package com.thora.core.math;

import java.util.Arrays;

public class IntArrVector implements IntVector {
	
	protected final int[] comps;
	
	IntArrVector(int[] comps) {
		this.comps = comps;
	}
	
	public IntArrVector(int x, int y) {
		this.comps = new int[] {x, y};
	}

	@Override
	public int getIX() {
		return comps[0];
	}

	@Override
	public int getIY() {
		return comps[1];
	}

	@Override
	public IntArrVector setX(int x) {
		this.comps[0] = x;
		return this;
	}

	@Override
	public IntArrVector setY(int y) {
		this.comps[1] = y;
		return this;
	}
	
	@Override
	public IntArrVector setAs(int x, int y) {
		this.comps[0] = x;
		this.comps[1] = y;
		return this;
	}
	
	public IntArrVector setAs(IntArrVector v) {
		this.comps[0] = v.getIX();
		this.comps[1] = v.getIY();
		return this;
	}
	
	public int[] comps() {
		return comps;
	}
	
	@Override
	public IntArrVector clone() {
		return new IntArrVector(Arrays.copyOf(comps, 2));
	}

}
