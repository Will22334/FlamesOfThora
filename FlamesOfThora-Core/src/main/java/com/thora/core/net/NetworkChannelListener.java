package com.thora.core.net;

public interface NetworkChannelListener {
	
	boolean register(NetworkChannel channel);
	
	boolean unRegister(NetworkChannel channel);
	
	void onOpen(NetworkChannel channel);
	
	void onClose(NetworkChannel channel);
	
	void handleMessage(NetworkChannel channel, Object msg);
	
}
