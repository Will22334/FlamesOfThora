package com.thora.core;

public interface ThoraColor {
	
	public static final ThoraColor WHITE = new FinalThoraColor(255, 255, 255);
	
	public static final ThoraColor RED = new FinalThoraColor(255,0,0);
	public static final ThoraColor GREEN = new FinalThoraColor(0,255,0);
	public static final ThoraColor BLUE = new FinalThoraColor(0,0,255);
	
	public static final ThoraColor BLACK = new FinalThoraColor(0, 0, 0);
	
	public static boolean verifyByte(final int b) {
		return b >= 0 && b < 255;
	}
	
	public static int mod(final int value, final String segment) {
		if(value < 0 || value > 255) {
			throw new IllegalArgumentException(String.format("RGBA: %s=%s is not within [0,255]", segment, value));
		}
		return value;
	}
	
	public static int mod(final int value) {
		return mod(value, "Some value");
	}
	
	public static int mod(final float value, final String segment) {
		if(value < 0 || value > 1f) {
			throw new IllegalArgumentException(String.format("RGBA: %s=%s is not within [0.0, 1.0]", segment, value));
		}
		return (int) (value * 255);
	}
	
	public static int mod(final float value) {
		return mod(value, "Some value");
	}
	
	public float getR();
	
	public default int getRi() {
		return (int)(255 * getR());
	}
	
	public float getG();
	
	public default int getGi() {
		return (int)(255 * getG());
	}
	
	public float getB();
	
	public default int getBi() {
		return (int)(255 * getB());
	}
	
	public float getA();
	
	public default int getAi() {
		return (int)(255 * getA());
	}
	
	public default ThoraColor alphaFactor(final float factor) {
		return new FinalThoraColor(getR(), getG(), getB(), (int)(factor * 255/getA()));
	}
	
}
