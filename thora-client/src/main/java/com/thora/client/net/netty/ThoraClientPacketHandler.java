package com.thora.client.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.LoginTransaction;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.net.netty.PodHandler;

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
		addHandler(new TileMessageConsumer());
	}
	
	public class LoginResponseConsumer extends MessageConsumer<LoginResponseMessage> {
		
		@Override
		public void consume(ChannelHandlerContext ctx, LoginResponseMessage message) {
			LoginTransaction t = getManager().loginTransaction;
			t.response = message;
			getManager().loginPromise.setSuccess(t);
			ctx.channel().writeAndFlush(new ChatMessage("Secret 2.1327"));
		}
		
	}
	
	public class ChatMessageConsumer extends MessageConsumer<ChatMessage> {

		@Override
		public void consume(ChannelHandlerContext ctx, ChatMessage message) {
			PlayerSession session = PlayerSession.findSession(ctx);
			logger().info("Got Message \"{}\" from {}", message.message, session);
		}
		
	}
	
	public class TileMessageConsumer extends MessageConsumer<BasicTileMessage> {

		@Override
		public void consume(ChannelHandlerContext ctx, BasicTileMessage message) {
			PlayerSession session = PlayerSession.findSession(ctx);
			logger().trace("Got {} of size {} from {}", message.getClass().getSimpleName(), message.size(), session);
		}
		
	}
	
}
