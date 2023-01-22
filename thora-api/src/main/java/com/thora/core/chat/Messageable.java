package com.thora.core.chat;

import com.thora.core.HasName;
import com.thora.core.net.message.ChatMessage;

/**
 * A unique Identity that can send and receive chat messages and is discernible towards clients.
 * This usually is players but could be a remote console or possibly a conversable NPC.
 * @author Dave
 *
 */
public interface Messageable extends HasName {
	
	public void sendMessage(ChatMessage message);
	
	public default void sendMessage(Messageable speaker, String message) {
		sendMessage(new ChatMessage(speaker, message));
	}
	
	public default void sendMessage(String message) {
		sendMessage(new ChatMessage(message));
	}
	
}
