package com.thora.core;

import com.google.common.base.Objects;
import com.thora.core.chat.ChatFormatter;

public class ThoraChatColor {
	
	public static ThoraChatColor create(final int r, final int g, final int b, final int a) {
		return new ThoraChatColor(modByte(r, "R"), modByte(g, "G"), modByte(b, "B"), modByte(b, "A"));
	}
	
	public static ThoraChatColor create(final int r, final int g, final int b) {
		return new ThoraChatColor(modByte(r, "R"), modByte(g, "G"), modByte(b, "B"), 255);
	}
	
	public static ThoraChatColor create(final float r, final float g, final float b, final float a) {
		return new ThoraChatColor(mod(r, "R"), mod(g, "G"), mod(b, "B"), mod(a, "A"));
	}
	
	public static ThoraChatColor create(final float r, final float g, final float b) {
		return new ThoraChatColor(mod(r, "R"), mod(g, "G"), mod(b, "B"), 255);
	}
	
	protected static final boolean verifyByte(final int b) {
		return b >= 0 && b < 255;
	}
	
	protected static final int modByte(final int value, final String segment) {
		if(value < 0 || value > 255) {
			throw new IllegalArgumentException(String.format("RGB: %s=%s is not within [0,255]", segment, value));
		}
		return value;
	}
	
	protected static final int mod(final int value) {
		return modByte(value, "Some value");
	}
	
	protected static final int mod(final float value, final String segment) {
		if(value < 0 || value > 1f) {
			throw new IllegalArgumentException(String.format("RGB: %s=%s is not within [0.0, 1.0]", segment, value));
		}
		return (int) (value * 255);
	}
	
	protected static final int mod(final float value) {
		return mod(value, "Some value");
	}
	
	private final int r, g, b, a;
	private String s;
	
	protected ThoraChatColor(final int r, final int g, final int b, final int a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	protected ThoraChatColor(final float r, final float g, final float b, final float a) {
		this((int)(r * 255f), (int)(g * 255f), (int)(b * 255f), (int)(a * 255f));
	}
	
	public final int getR() {
		return r;
	}

	public final int getG() {
		return g;
	}

	public final int getB() {
		return b;
	}

	public final int getA() {
		return a;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(r, g, b, a);
	}

	@Override
	public boolean equals(final Object obj) {
		if(obj == null) {
			return false;
		}
		
		if(obj instanceof ThoraChatColor) {
			final ThoraChatColor o = (ThoraChatColor) obj;
			return this.getR() == o .getR()
					&& this.getG() == o.getG()
					&& this.getB() == o.getB()
					&& this.getA() == o.getA();
		}
		return false;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return new ThoraChatColor(getR(), getG(), getB(), getA());
	}

	@Override
	public String toString() {
		if(s == null) {
			s = ChatFormatter.color(getR(), getG(), getB(), getA());
		}
		return s;
	}
	
}
