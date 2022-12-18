package com.thora.login.impl;

import com.thora.login.ILoginResponse;

public class BasicLoginResponse implements ILoginResponse {
	
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
