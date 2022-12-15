package com.thora.core.math;

import java.util.Arrays;

public class DoubleArrVector implements Vector<DoubleArrVector>{
	
	protected final double[] comps;
	
	public DoubleArrVector(double[] comps) {
		this.comps = comps;
	}
	
	public DoubleArrVector(double x, double y) {
		this(new double[] {x, y});
	}

	@Override
	public double getX() {
		return comps[0];
	}

	@Override
	public double getY() {
		return comps[1];
	}

	@Override
	public int getIX() {
		return (int) Math.round(getX());
	}

	@Override
	public int getIY() {
		return (int) Math.round(getY());
	}

	@Override
	public DoubleArrVector setX(double x) {
		this.comps[0] = x;
		return this;
	}

	@Override
	public DoubleArrVector setY(double y) {
		this.comps[1] = y;
		return this;
	}

	@Override
	public DoubleArrVector setX(int x) {
		this.comps[0] = x;
		return this;
	}

	@Override
	public DoubleArrVector setY(int y) {
		this.comps[1] = y;
		return this;
	}

	@Override
	public DoubleArrVector setAs(double x, double y) {
		this.comps[0] = x;
		this.comps[1] = y;
		return this;
	}

	public double[] comps() {
		return comps;
	}
	
	public double[] copyComps() {
		return Arrays.copyOf(comps, 2);
	}
	
	@Override
	public DoubleArrVector clone() {
		return new DoubleArrVector(Arrays.copyOf(comps, 2));
	}
	
}
