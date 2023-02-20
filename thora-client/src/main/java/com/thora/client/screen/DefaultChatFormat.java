package com.thora.client.screen;

import com.badlogic.gdx.graphics.Color;
import com.thora.client.ColoredMessagable;
import com.thora.core.net.message.ChatMessage;

public class DefaultChatFormat implements ChatFormatter {
	
	public static final DefaultChatFormat INSTANCE = new DefaultChatFormat();
	
	public static String markupColor(final Color color) {
		return "[#" + color.toString() + "]";
	}
	
	public static final StringBuilder markupColor(final StringBuilder b, final Color color) {
		return b.append("[#").append(color.toString()).append("]");
	}
	
	protected DefaultChatFormat() {
		
	}
	
	@Override
	public final String format(final ChatMessage message) {
		final StringBuilder b = new StringBuilder();
		
		markupColor(b, Color.GREEN).append("[").append(DATE_TIME_ISO_8601.format(message.time)).append("]");
		
		if(message.sender instanceof ColoredMessagable) {
			ColoredMessagable sender = (ColoredMessagable) message.sender;
			b.append(markupColor(sender.getColor())).append(sender.getName()).append("[]");
			b.append("[] :  ");
		} else {
			b.append("[]  ");
		}
		
		b.append(message.content);
		
		//		b.append(markupColor(Color.GREEN)).append("[").append(DATE_TIME_ISO_8601.format(message.time)).append("]");
		//		b.append("  ");
		//		if(message.sender instanceof ColoredMessagable) {
		//			ColoredMessagable sender = (ColoredMessagable) message.sender;
		//			b.append(markupColor(sender.getColor())).append(sender.getName()).append("[]");
		//		}
		//		
		//		b.append("[] :  ").append(message.content);
		
		return b.toString();
	}
	
}
