package com.thora.core.net;

import java.util.concurrent.Future;

public abstract class AsyncNetworkChannel extends NetworkChannel {
	
	@Override
	public abstract Future<?> read();
	
	@Override
	public abstract Future<?> write(Object msg);
	
	@Override
	public abstract Future<?> writeAndFlush(Object msg);
	
	@Override
	public abstract Future<?> flush();
	
	@Override
	public abstract Future<?> close();
	
}
