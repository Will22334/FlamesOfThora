package com.thora.core.net.message;

import java.time.Instant;

import com.thora.core.chat.Messageable;

public class ChatMessage extends AbstractThoraMessage {
	
	public static final String COMMAND_PREFIX = "/";
	
	public final Instant time;
	public final Messageable sender;
	public final String message;
	
	public ChatMessage(final Instant time, final Messageable sender, final String message) {
		this.time = time;
		this.sender = sender;
		this.message = message;
	}
	
	public ChatMessage(final Messageable sender, final String message) {
		this(Instant.now(), sender, message);
	}
	
	public ChatMessage(final String message) {
		this(Instant.now(), null, message);
	}
	
	public boolean isCommand() {
		return message.startsWith(COMMAND_PREFIX);
	}
	
}
