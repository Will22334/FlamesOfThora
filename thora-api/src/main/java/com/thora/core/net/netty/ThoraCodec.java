package com.thora.core.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.message.ThoraMessage;

public abstract class ThoraCodec extends PodCodec<ThoraMessage> {
	
	public static final int MAX_FRAME_SIZE = 5_000;
	
	public static final int OPCODE_SERVER_LOGIN_REQUEST = 0;
	
	public static final int OPCODE_CLIENT_LOGIN_RESPONSE = 1;
	
	public ThoraCodec(Logger logger) {
		super(logger);
	}
	
	@Override
	protected abstract void populate();
	
}
