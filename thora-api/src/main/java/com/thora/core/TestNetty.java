package com.thora.core;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TestNetty {
	
	public static final int DEFAULT_SIZE = 8 * 1024;
	
	private static final ByteBuf createNewBuf() {
		return Unpooled.buffer(DEFAULT_SIZE);
	}
	
	protected static final ThreadLocal<ByteBuf> buffers = ThreadLocal.withInitial(TestNetty::createNewBuf);
	
	public static final ByteBuf getCurrentBuf() {
		return buffers.get();
	}
	
	public static final ByteBuf getBuf() {
		return getCurrentBuf().clear();
	}
	
}
