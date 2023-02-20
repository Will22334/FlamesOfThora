package com.thora.core.chat;

import com.thora.core.net.message.ChatMessage;

/**
 * A {@link Messageable} that can execute textual commands and receive textual messages.
 * @author Dave
 *
 */
public interface CommandCaller extends Messageable {
	
	public static final String COLOR_RED = "[#ff0000ff]";
	
	public void executeCommand(final String commandText);

	public default void sendErrorMessage(final String errorMessage) {
		this.sendMessage(new ChatMessage(COLOR_RED + errorMessage));
	}
	
}
