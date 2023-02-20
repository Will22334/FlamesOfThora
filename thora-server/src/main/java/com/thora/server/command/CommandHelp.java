package com.thora.server.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Sets;
import com.thora.core.chat.CommandCaller;

public class CommandHelp extends Command {
	
	public static final String NAME = "help";
	public static final String DESC = "List all players currently logged in.";
	public static final Set<String> aliases = Collections.unmodifiableSet(Sets.newLinkedHashSet(Arrays.asList("man","?")));
	
	CommandHelp() {
		super(NAME, DESC, Command.EMPTY_MANUAL, aliases);
	}

	@Override
	protected boolean execute(final CommandManager manager, final CommandCaller caller, final String text, final String cmd,
			final List<String> args) {
		if(args == null || args.isEmpty()) {
			caller.sendErrorMessage("");
		}
		final Command queryCommand = manager.findCommand(args.get(0));
		return false;
	}
	
}
