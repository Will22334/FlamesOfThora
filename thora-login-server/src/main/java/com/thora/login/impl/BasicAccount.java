package com.thora.login.impl;

import java.time.Instant;
import java.util.UUID;

import com.thora.login.IAccount;

public class BasicAccount implements IAccount {
	
	private String username;
	private Instant creationTime;
	
	public BasicAccount(UUID id, String email, String username, Instant creationTime) {
		this.username = username;
		this.creationTime = creationTime;
	}

	@Override
	public String getUsername() {
		return username;
	}
	
	public Instant getCreationTime() {
		return creationTime;
	}
	
}
