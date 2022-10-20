package com.thora.core.net;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractNetworkChannelListener<C extends NetworkChannel> implements NetworkChannelListener {
	
	protected Collection<C> channels = new HashSet<>();
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean onRegister(NetworkChannel channel) {
		return channels.add((C) channel);
	}
	
	@Override
	public boolean onUnRegister(NetworkChannel channel) {
		return channels.remove(channel);
	}

	@Override
	public boolean onClose(NetworkChannel channel) {
		channels.remove(channel);
		return true;
	}
	
}
