package com.thora.core.net.netty;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.concurrent.Future;

import com.thora.core.net.AsyncNetworkChannel;
import com.thora.core.net.NetworkChannelListener;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;

public class NettyNetworkChannel extends AsyncNetworkChannel {
	
	private SocketChannel nettyChannel;
	
	public NettyNetworkChannel(SocketChannel channel) {
		super();
		this.nettyChannel = Objects.requireNonNull(channel, () -> "Cannot create " + NettyNetworkChannel.class.getSimpleName() + " with null netty channel!");
	}
	
	public NettyNetworkChannel(Channel channel) {
		this((SocketChannel)channel);
	}
	
	public final SocketChannel rawChannel() {
		return nettyChannel;
	}

	@Override
	public boolean isOpen() {
		return rawChannel().isActive();
	}
	
	@Override
	public InetSocketAddress localAddress() {
		return rawChannel().localAddress();
	}
	
	@Override
	public InetSocketAddress remoteAddress() {
		return rawChannel().remoteAddress();
	}
	
	public ChannelPromise voidPromise() {
		return rawChannel().voidPromise();
	}
	
	@Override
	public Future<?> read() {
		throw new UnsupportedOperationException(String.format("AsyncPollRead is not supported for %s. Register channel to a %s instead.",
				getClass().getSimpleName(), NetworkChannelListener.class.getSimpleName()));
	}
	
	public ChannelFuture write(Object msg, ChannelPromise p) {
		return rawChannel().write(msg, p);
	}
	
	@Override
	public ChannelFuture write(Object msg) {
		return rawChannel().write(msg);
	}
	
	public ChannelFuture writeAndFlush(Object msg, ChannelPromise p) {
		return rawChannel().writeAndFlush(msg, p);
	}
	
	@Override
	public ChannelFuture writeAndFlush(Object msg) {
		return rawChannel().writeAndFlush(msg);
	}

	@Override
	public ChannelFuture flush() {
		rawChannel().flush();
		return rawChannel().newSucceededFuture();
	}
	
	@Override
	public ChannelFuture close() {
		return rawChannel().close();
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(nettyChannel);
	}

	@Override
	public boolean equals(Object o) {
		if(o instanceof NettyNetworkChannel) {
			NettyNetworkChannel c = (NettyNetworkChannel) o;
			return Objects.equals(nettyChannel, c.nettyChannel);
		}
		return false;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[" + rawChannel() + "]";
	}
	
}
