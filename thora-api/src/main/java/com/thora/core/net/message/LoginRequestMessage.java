package com.thora.core.net.message;

public class LoginRequestMessage extends AbstractThoraMessage {
	
	public final String username, password;
	public final long sessionKey, timeStamp;
	
	public LoginRequestMessage(String username, String password, long sessionKey, long timeStamp) {
		super();
		this.username = username;
		this.password = password;
		this.timeStamp = timeStamp;
		this.sessionKey = sessionKey;
	}
	
	public LoginRequestMessage(String username, String password, long sessionKey) {
		this(username, password, sessionKey, System.currentTimeMillis());
	}
	
}
