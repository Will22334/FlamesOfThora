package com.thora.server.command.all;

import java.util.List;

import com.thora.core.chat.CommandCaller;
import com.thora.core.world.Locatable;
import com.thora.server.command.Command;
import com.thora.server.command.CommandManager;

public class CommandPosition extends Command {
	
	public static final String NAME = "position";
	public static final String DESC = "Returns the current position of the CommandCaller(Caller may not be Locatable).";
	public static final String MANUAL = "NO Manual!";
	
	public CommandPosition() {
		super(NAME, DESC, MANUAL, new String[]{"pos", "loc", "location", "whereami", "where"});
	}
	
	@Override
	protected boolean execute(final CommandManager manager, final CommandCaller caller, final String text, final String cmd,
			final List<String> args) {
		if(caller instanceof Locatable) {
			final Locatable l = ((Locatable) caller).getLocation();
			caller.sendMessage(String.format("Position: %s = %s", caller.getName(), l.getLocation()));
			return true;
		} else {
			caller.sendErrorMessage("You are located " + "NOWHERE!");
			return false;
		}
	}
	
}
