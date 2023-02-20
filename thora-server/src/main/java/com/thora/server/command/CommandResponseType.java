package com.thora.server.command;

public enum CommandResponseType {
	
	OK(true),
	BAD_FORMAT(false),
	NOT_EXIST(false);
	
	CommandResponseType(final boolean isSuccessful) {
		this.isSuccessful = isSuccessful;
	}
	
	private final boolean isSuccessful;
	
	public final boolean isSuccessful() {
		return isSuccessful;
	}
	
}
