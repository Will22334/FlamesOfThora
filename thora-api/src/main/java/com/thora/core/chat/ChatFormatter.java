package com.thora.core.chat;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.badlogic.gdx.graphics.Color;
import com.thora.core.ThoraChatColor;
import com.thora.core.ThoraColor;
import com.thora.core.net.message.ChatMessage;

public interface ChatFormatter {
	
	public static final DateTimeFormatter DATE_TIME_ISO_8601 = DateTimeFormatter.ISO_LOCAL_TIME
			.withZone(ZoneId.systemDefault());
	
	public static String color(final ThoraChatColor c) {
		return color(c.getR(), c.getG(), c.getB(), c.getA());
	}
	
	public static String color(final int r, final int g, final int b, final int a) {
		return String.format("[#%02x%02x%02x%02x]", r, g, b, a);
	}
	
	public static String color(final int r, final int g, final int b) {
		return color(r, g, b, Byte.MAX_VALUE);
	}
	
	public static String colorInt(final ThoraColor c) {
		return color(c.getRi(), c.getGi(), c.getBi(), c.getAi());
	}
	
	public static StringBuilder color(final StringBuilder b, final Color c) {
		return b.append("[#").append(c).append("]");
	}
	
	public static String color(final Color c) {
		return "[#" + c + "]";
	}
	
	public String format(ChatMessage message);
	
}
