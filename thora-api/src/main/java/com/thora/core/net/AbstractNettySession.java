package com.thora.core.net;

import com.thora.core.net.netty.NettyNetworkChannel;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

public abstract class AbstractNettySession extends NetworkSession {
	
	private CryptographicCredentials creds;
	private NettyNetworkChannel channel;
	private long lastResponse = -1L;
	
	public AbstractNettySession(NettyNetworkChannel netChannel) {
		super();
		this.channel = netChannel;
		this.creds = new SimpleCryptographicCredentials();
	}
	
	public AbstractNettySession(SocketChannel channel) {
		this(new NettyNetworkChannel(channel));
	}
	
	public SocketChannel rawChannel() {
		return channel.rawChannel();
	}
	
	protected <T> Attribute<T> attr(AttributeKey<T> attributeKey) {
		return rawChannel().attr(attributeKey);
	}
	
	@Override
	public long lastResponse() {
		return lastResponse;
	}

	protected final void setLastResponse(long lastResponse) {
		this.lastResponse = lastResponse;
	}

	@Override
	public CryptographicCredentials getCryptoCreds() {
		return creds;
	}

	@Override
	public NettyNetworkChannel getChannel() {
		return channel;
	}
	
}
