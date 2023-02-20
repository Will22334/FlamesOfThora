package com.thora.server.command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.thora.core.chat.CommandCaller;
import com.thora.server.ThoraServer;
import com.thora.server.command.all.CommandPosition;

public class CommandManager implements CommandExecutor {
	
	private static final String REGEX = "\"[^\"]+\"|[\\w]+";
	
	private final ThoraServer server;
	
	private final Map<String,Command> commands = new HashMap<>();
	
	public CommandManager(final ThoraServer server) {
		this.server = server;
		init();
	}
	
	protected ThoraServer server() {
		return server;
	}
	
	protected void init() {
		this.addCommand(new CommandListPlayers());
		this.addCommand(new CommandHelp());
		this.addCommand(new CommandPosition());
	}
	
	public void addCommand(final Command cmd) {
		for(String name: cmd.getNames()) {
			commands.put(name, cmd);
		}
	}
	
	Command findCommand(final String name) {
		return commands.get(name);
	}
	
	@Override
	public void executeCommand(final CommandCaller sender, String text) {
		if(text == null) {
			
		}
		text = text.substring(1);
		if(text.trim().isEmpty()) return;
		
		final Matcher m = Pattern.compile(REGEX).matcher(text);
		
		if(m.find()) {
			String value = m.group();
			final List<String> argsList = new ArrayList<String>();
			final String cmdAlias = value;
			while(m.find()) {
				if(value.startsWith("\"") && value.endsWith("\"")) {
					value = value.substring(1, value.length()-1);
				}
				argsList.add(value);
			}
			
			final Command cmd = this.findCommand(cmdAlias);
			
			if(cmd != null) {
				cmd.execute(this, sender, text, cmdAlias, argsList);
			} else {
				sender.sendErrorMessage("Invalid Command := " + cmdAlias);
			}
		}
		
	}
	
}
