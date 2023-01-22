package com.thora.client;

import com.badlogic.gdx.graphics.Color;
import com.thora.core.chat.Messageable;
import com.thora.core.net.message.ChatMessage;

public class ColoredMessagable implements Messageable {
	
	private Color color;
	private String name;
	
	public ColoredMessagable(final Color color, final String name) {
		this.color = color;
		this.name = name;
	}
	
	public ColoredMessagable(final String name) {
		this(null, name);
	}
	
	public Color getColor() {
		return color;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public void sendMessage(final ChatMessage message) {
		
	}
	
}
