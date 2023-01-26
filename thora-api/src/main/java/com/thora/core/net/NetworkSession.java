package com.thora.core.net;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.AttributeKey;

public abstract class NetworkSession implements HasCryptographicCredentials {
	
	public static final AttributeKey<NetworkSession> ATTRIBUTE_SESSION = AttributeKey.newInstance("SESSION");
	
	public static final <S extends NetworkSession> S findSession(ChannelHandlerContext ctx) {
		return findSession(ctx.channel());
	}
	
	@SuppressWarnings("unchecked")
	public static final <S extends NetworkSession> S findSession(Channel channel) {
		return (S) channel.attr(ATTRIBUTE_SESSION).get();
	}
	
	public NetworkSession() {
		super();
	}
	
	protected final <S extends NetworkSession> boolean tryAttach(Channel channel) {
		return channel.attr(ATTRIBUTE_SESSION).compareAndSet(null, this);
	}
	
	public abstract NetworkChannel getChannel();
	
	public InetSocketAddress localAddress() {
		return getChannel().localAddress();
	}
	
	public InetSocketAddress remoteAddress() {
		return getChannel().remoteAddress();
	}
	
	public abstract long lastResponse();
	
	public Object write(Object msg) {
		return getChannel().write(msg);
	}
	
	public Object writeAndFlush(Object msg) {
		return getChannel().writeAndFlush(msg);
	}

	@Override
	public String toString() {
		return "[" + localAddress() + " : " + remoteAddress() + "]";
	}
	
}
