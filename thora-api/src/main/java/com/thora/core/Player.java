package com.thora.core;

import com.thora.core.world.Locatable;

/**
 * A online and logged in {@link Account} instance.
 * @author Dave
 *
 */
public interface Player extends Locatable {
	
	String getUsername();
	
}
