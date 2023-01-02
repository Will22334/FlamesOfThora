package com.thora.login;

import java.time.Instant;

/**
 * A user/player account that holds only account information and
 * does not represent a online player login session instance.
 * @author Dave
 *
 */
public interface Account {
	
	public String getUsername();
	
	public Instant getCreationTime();
	
}
