package com.thora.core.chat;

import com.thora.core.HasName;
import com.thora.core.net.message.ChatMessage;

/**
 * A unique Identity that can send and receive textual(chat) messages and is discernible between clients. <br>
 * This could be a Player, NPC, Console, etc.
 * @author Dave
 *
 */
public interface Messageable extends HasName {
	
	public void sendMessage(final ChatMessage message);
	
	public default void sendMessage(final Messageable speaker, final String message) {
		sendMessage(new ChatMessage(speaker, message));
	}
	
	public default void sendMessage(final String message) {
		sendMessage(new ChatMessage(message));
	}
	
}
