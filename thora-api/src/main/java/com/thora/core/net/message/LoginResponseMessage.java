package com.thora.core.net.message;

public class LoginResponseMessage extends AbstractThoraMessage {
	
	public boolean accepted;
	public String reason;
	
	public LoginResponseMessage(boolean accepted, String reason) {
		super();
		this.accepted = accepted;
		this.reason = reason;
	}
	
	public final boolean isAccepted() {
		return accepted;
	}
	
	public final LoginResponseMessage setAccepted(boolean accepted) {
		this.accepted = accepted;
		return this;
	}
	
	public final String getReason() {
		return reason;
	}
	
	public final LoginResponseMessage setReason(String reason) {
		this.reason = reason;
		return this;
	}
	
}
