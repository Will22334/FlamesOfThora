package com.thora.core.net;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public abstract class NetworkChannel {
	
	private Collection<NetworkChannelListener> listeners = new HashSet<>();
	private Collection<NetworkChannelListener> safeListeners = Collections.unmodifiableCollection(listeners);
	
	public Collection<NetworkChannelListener> getListeners() {
		return safeListeners;
	}
	
	protected boolean addListener(NetworkChannelListener l) {
		return listeners.add(l);
	}
	
	protected boolean removeListener(NetworkChannelListener l) {
		return listeners.remove(l);
	}
	
	public abstract boolean isOpen();
	
	public abstract InetSocketAddress localAddress();
	
	public abstract InetSocketAddress remoteAddress();
	
	public abstract Object read();
	
	public abstract Object write(Object msg);
	
	public abstract Object writeAndFlush(Object msg);
	
	public abstract Object flush();
	
	public abstract Object close();
	
}
