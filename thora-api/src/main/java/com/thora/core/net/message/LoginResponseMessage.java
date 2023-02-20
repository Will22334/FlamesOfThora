package com.thora.core.net.message;

import com.google.common.base.Objects;

public class LoginResponseMessage extends AbstractThoraMessage {
	
	protected boolean accepted;
	protected String reason;
	
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

	@Override
	public int hashCode() {
		return Objects.hashCode(accepted, reason);
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		if(this == obj)
			return true;
		if(obj instanceof LoginResponseMessage) {
			LoginResponseMessage m = (LoginResponseMessage) obj;
			return Objects.equal(isAccepted(), m.isAccepted())
					&& Objects.equal(getReason(), m.getReason());
		}
		return false;
	}

	@Override
	public String toString() {
		return String.format("[(%s) %s: %s]", getClass().getSimpleName(), isAccepted(), getReason());
	}
	
}
