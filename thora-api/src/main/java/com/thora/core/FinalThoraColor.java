package com.thora.core;

import com.google.common.base.Objects;
import com.thora.core.chat.ChatFormatter;

public class FinalThoraColor implements ThoraColor {
	
	public static final FinalThoraColor create(final int r, final int g, final int b, final int a) {
		return new FinalThoraColor(ThoraColor.mod(r, "R"),
				ThoraColor.mod(g, "G"),
				ThoraColor.mod(b, "B"),
				ThoraColor.mod(a, "A"));
	}
	
	public static final FinalThoraColor create(final int r, final int g, final int b) {
		return new FinalThoraColor(ThoraColor.mod(r, "R"),
				ThoraColor.mod(g, "G"),
				ThoraColor.mod(b, "B"),
				255);
	}
	
	public static final FinalThoraColor create(final float r, final float g, final float b, final float a) {
		return new FinalThoraColor(ThoraColor.mod(r, "R"),
				ThoraColor.mod(g, "G"),
				ThoraColor.mod(b, "B"),
				ThoraColor.mod(a, "A"));
	}
	
	public static final FinalThoraColor create(final float r, final float g, final float b) {
		return new FinalThoraColor(ThoraColor.mod(r, "R"),
				ThoraColor.mod(g, "G"),
				ThoraColor.mod(b, "B"),
				255);
	}
	
	private final float r, g, b, a;
	
	protected FinalThoraColor(final float r, final float g, final float b, final float a) {
		this.r = r;
		this.g = g;
		this.b = b;
		this.a = a;
	}
	
	protected FinalThoraColor(final float r, final float g, final float b) {
		this(r, g, b, 1f);
	}
	
	protected FinalThoraColor(final int r, final int g, final int b, final int a) {
		this.r = r / 255f;
		this.g = g / 255f;
		this.b = b / 255f;
		this.a = a / 255f;
	}
	
	protected FinalThoraColor(final int r, final int g, final int b) {
		this(r, g, b, 255);
	}
	
	@Override
	public float getR() {
		return r;
	}
	
	@Override
	public float getG() {
		return g;
	}
	
	@Override
	public float getB() {
		return b;
	}
	
	@Override
	public float getA() {
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
		return new FinalThoraColor(getR(), getG(), getB(), getA());
	}

	@Override
	public String toString() {
		return ChatFormatter.color(getRi(), getGi(), getBi(), getAi());
	}
	
}
