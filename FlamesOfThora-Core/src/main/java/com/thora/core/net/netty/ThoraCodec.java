package com.thora.core.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.ThoraMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ThoraCodec extends PodCodec<ThoraMessage> {
	
	public static final int OPCODE_LOGIN_REQUEST = 0;
	
	public ThoraCodec(Logger logger) {
		super(logger);
	}
	
	@Override
	protected void populate() {
		
	}
	
	
	
	public class LoginRequestEncoder extends MessageEncoder<LoginRequestMessage> {
		protected LoginRequestEncoder() {
			super(OPCODE_LOGIN_REQUEST);
		}
		@Override
		public void encode(ChannelHandlerContext ctx, LoginRequestMessage obj, ByteBuf buf) {
			// TODO Auto-generated method stub
		}
	}
	
}
