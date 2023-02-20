package com.thora.core.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.LoginTransaction;
import com.thora.core.net.message.ChatMessage;
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
		addHandler(new ChatMessageConsumer());
	}
	
	public class LoginResponseConsumer extends MessageConsumer<LoginResponseMessage> {
		
		@Override
		public void consume(final ChannelHandlerContext ctx, final LoginResponseMessage message) {
			LoginTransaction t = getManager().loginTransaction;
			if(t.response == null) {
				t.response = message;
				
				getManager().loginPromise.setSuccess(t);
			} else {
				logger().warn("Recieved an additional {} after being logged in.", message);
			}
			
		}
		
	}
	
	public class ChatMessageConsumer extends MessageConsumer<ChatMessage> {

		@Override
		public void consume(ChannelHandlerContext ctx, ChatMessage message) {
			PlayerSession session = PlayerSession.findSession(ctx);
			logger().info("Got Message \"{}\" from {}", message.content, session);
		}
		
	}
	
}
