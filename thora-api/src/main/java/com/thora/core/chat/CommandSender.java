package com.thora.core.chat;

/**
 * A {@link Messageable} that can execute textual commands along side normal messaging.
 * @author Dave
 *
 */
public interface CommandSender extends Messageable {
	
	public void executeCommand(String commandText);
	
}
