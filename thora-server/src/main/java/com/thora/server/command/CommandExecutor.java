package com.thora.server.command;

import com.thora.core.chat.CommandCaller;

public interface CommandExecutor {
	
	public void executeCommand(final CommandCaller sender, final String commandText);
	
}
