package com.thora.core.net;

public abstract class NetworkSession implements HasCryptographicCredentials {
	
	public abstract NetworkChannel getChannel();
	
}
