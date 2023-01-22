package com.thora.core;

import com.thora.core.chat.CommandSender;
import com.thora.core.world.Locatable;

/**
 * A online and logged in {@link Account} instance.
 * @author Dave
 *
 */
public interface Player extends CommandSender, Locatable {
	
	String getUsername();
	
	@Override
	public default String getName() {
		return getUsername();
	}
	
}
