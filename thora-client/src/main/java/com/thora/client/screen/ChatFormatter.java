package com.thora.client.screen;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.badlogic.gdx.graphics.Color;
import com.thora.core.net.message.ChatMessage;

public interface ChatFormatter {
	
	public static final ChatFormatter DEFAULT = DefaultChatFormat.INSTANCE;
	
	public static final DateTimeFormatter DATE_TIME_ISO_8601 = DateTimeFormatter.ISO_LOCAL_TIME
			.withZone(ZoneId.systemDefault());
	
	public static String color(final int r, final int g, final int b, final int a) {
		return "[#" + Integer.toHexString(r) + Integer.toHexString(g) + Integer.toHexString(b) + Integer.toHexString(a);
	}
	
	public static String color(final int r, final int g, final int b) {
		return color(r, g, b, 0xff);
	}
	
	public static StringBuilder color(final StringBuilder b, final Color c) {
		return b.append("[#").append(c).append("]");
	}
	
	public static String color(final Color c) {
		return "[#" + c + "]";
	}
	
	public String format(ChatMessage message);
	
}
