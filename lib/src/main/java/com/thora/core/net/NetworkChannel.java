package com.thora.core.net;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashSet;

public abstract class NetworkChannel {
	
	private Collection<NetworkChannelListener> listeners = new HashSet<>();
	
	public abstract boolean isOpen();
	
	protected Collection<NetworkChannelListener> getListeners() {
		return listeners;
	}
	
	protected boolean addListener(NetworkChannelListener l) {
		return listeners.add(l);
	}
	
	protected boolean removeListener(NetworkChannelListener l) {
		return listeners.remove(l);
	}
	
	public abstract InetSocketAddress localAddress();
	
	public abstract InetSocketAddress remoteAddress();
	
	protected void fireRead(Object msg) {
		for(NetworkChannelListener l: getListeners()) {
			l.handleMessage(this, msg);
		}
	}
	
	public abstract Object read();
	
	public abstract Object write(Object msg);
	
	public Object writeAndFlush(Object msg) {
		Object o = write(msg);
		flush();
		return o;
	}
	
	public abstract Object flush();
	
	public abstract Object close();
	
}
