package com.thora.core.net;

public interface NetworkChannelListener {
	
	default boolean onRegister(NetworkChannel channel) {
		return false;
	}
	
	default boolean onUnRegister(NetworkChannel channel) {
		return false;
	}
	
	default boolean onOpen(NetworkChannel channel) {
		return false;
	}
	
	default boolean onClose(NetworkChannel channel) {
		return true;
	}
	
}
