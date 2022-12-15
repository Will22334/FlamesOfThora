package com.thora.core.net.message;

public class ChatMessage extends AbstractThoraMessage {
	
	public final String message;
	
	public ChatMessage(String message) {
		this.message = message;
	}
	
}
