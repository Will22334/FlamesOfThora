package com.thora.server.command;

public class CommandResponse {
	
	final CommandResponseType type;
	final String message;
	
	public CommandResponse(final CommandResponseType type, final String message) {
		this.type = type;
		this.message = message;
	}
	
	public CommandResponseType getType() {
		return type;
	}
	
	public String getMessage() {
		return message;
	}
	
	
	
}
