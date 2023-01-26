package com.thora.server.command;

import com.thora.core.chat.CommandSender;

public interface CommandExecutor {
	
	public void executeCommand(CommandSender sender, String commandText);
	
}
