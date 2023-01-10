package com.thora.core.net.message;

public class StateChangeMessage extends AbstractThoraMessage {
	
	public final int stateID;
	
	public StateChangeMessage(int stateID) {
		this.stateID = stateID;
	}
	
}
