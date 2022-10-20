package com.thora.core.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.LoginTransaction;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.ThoraMessage;

import io.netty.channel.ChannelHandlerContext;

public class ThoraClientPacketHandler extends PodHandler<ThoraMessage> {
	
	private final NettyNetworkManager manager;
	
	protected ThoraClientPacketHandler(NettyNetworkManager manager, Logger logger) {
		super(logger);
		this.manager = manager;
	}
	
	protected final NettyNetworkManager getManager() {
		return manager;
	}
	
	@Override
	protected void populate() {
		addHandler(new LoginResponseConsumer());
	}
	
	public class LoginResponseConsumer extends MessageConsumer<LoginResponseMessage> {
		
		@Override
		public void consume(ChannelHandlerContext ctx, LoginResponseMessage message) {
			LoginTransaction t = getManager().loginTransaction;
			t.response = message;
			getManager().loginPromise.setSuccess(t);
		}
		
	}
	
}
