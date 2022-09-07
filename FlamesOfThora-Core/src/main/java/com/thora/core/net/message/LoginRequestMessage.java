package com.thora.core.net.message;

public class LoginRequestMessage implements ThoraMessage {
	
	public final String username, password;
	public final long timeStamp, sessionKey;
	
	public LoginRequestMessage(String username, String password, long timeStamp, long sessionKey) {
		super();
		this.username = username;
		this.password = password;
		this.timeStamp = timeStamp;
		this.sessionKey = sessionKey;
	}
	
	public LoginRequestMessage(String username, String password, long sessionKey) {
		this(username, password, System.currentTimeMillis(), sessionKey);
	}
	
}
