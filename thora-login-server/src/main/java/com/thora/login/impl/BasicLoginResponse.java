package com.thora.login.impl;

import com.thora.login.LoginResponse;

public class BasicLoginResponse implements LoginResponse {
	
	protected boolean accepted;
	protected String reason;
	
	public BasicLoginResponse(boolean accepted, String reason) {
		this.accepted = accepted;
		this.reason = reason;
	}
	
	public BasicLoginResponse() {
		
	}

	@Override
	public boolean isAccepted() {
		return accepted;
	}

	@Override
	public String getReason() {
		return reason;
	}

}
