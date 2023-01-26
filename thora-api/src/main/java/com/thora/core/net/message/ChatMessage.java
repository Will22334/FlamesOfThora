package com.thora.core.net.message;

import java.time.Instant;

import com.thora.core.FlamesOfThora;
import com.thora.core.chat.Messageable;

public class ChatMessage extends AbstractThoraMessage {
	
	public static final String COMMAND_PREFIX = "/";
	
	public static final String formatEscapes(final String text) {
		final String newText = text.replace("\\n", "\n");
		FlamesOfThora.logger.info("Formatted {}  into  {}", text, newText);
		return newText;
	}
	
	public final Instant time;
	public final Messageable sender;
	public final String content;
	
	public ChatMessage(final Instant time, final Messageable sender, final String content) {
		this.time = time;
		this.sender = sender;
		this.content = content;
	}
	
	public ChatMessage(final Messageable sender, final String content) {
		this(Instant.now(), sender, content);
	}
	
	public ChatMessage(final String content) {
		this(Instant.now(), null, content);
	}
	
	public Instant getTime() {
		return time;
	}
	
	public Messageable getSender() {
		return sender;
	}
	
	public String getContent() {
		return content;
	}
	
	public boolean isCommand() {
		return content.startsWith(COMMAND_PREFIX);
	}
	
	public boolean isChat() {
		return !isCommand();
	}
	
	public String getSenderName() {
		if(sender == null) return null;
		return sender.getName();
	}
	
}
