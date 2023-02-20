package com.thora.core;

import com.thora.core.chat.CommandCaller;
import com.thora.core.world.Locatable;

/**
 * A online and logged in {@link Account} instance.
 * @author Dave
 *
 */
public interface Player extends CommandCaller, Locatable {
	
	public String getUsername();
	
	@Override
	public default String getName() {
		return getUsername();
	}
	
}
