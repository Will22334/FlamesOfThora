package com.thora.core.net.netty;

import java.net.InetSocketAddress;
import java.util.concurrent.Future;

import com.thora.core.net.AsyncNetworkChannel;
import com.thora.core.net.NetworkChannelListener;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.SocketChannel;

public class NettyNetworkChannel<C extends SocketChannel> extends AsyncNetworkChannel {
	
	private C channel;
	
	protected NettyNetworkChannel(C channel) {
		this.channel = channel;
	}
	
	protected C rawChannel() {
		return channel;
	}

	@Override
	public boolean isOpen() {
		return rawChannel().isOpen();
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
		return rawChannel().write(msg, p);
	}
	
	@Override
	public ChannelFuture writeAndFlush(Object msg) {
		return rawChannel().write(msg);
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
	
}
