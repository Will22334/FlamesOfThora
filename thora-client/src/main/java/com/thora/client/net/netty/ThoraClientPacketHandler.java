package com.thora.client.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.client.FlamesOfThoraClient;
import com.thora.core.net.LoginTransaction;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.net.message.WorldDefinitionMessage;
import com.thora.core.net.netty.PodHandler;

import io.netty.channel.ChannelHandlerContext;

public class ThoraClientPacketHandler extends PodHandler<ThoraMessage> {
	
	private final FlamesOfThoraClient client;
	private final NettyNetworkManager manager;
	
	protected ThoraClientPacketHandler(NettyNetworkManager manager, Logger logger) {
		super(logger);
		this.manager = manager;
		this.client = manager.client();
	}
	
	protected final NettyNetworkManager getManager() {
		return manager;
	}
	
	protected final FlamesOfThoraClient client() {
		return client;
	}
	
	@Override
	protected void populate() {
		addHandler(new LoginResponseConsumer());
		addHandler(new ChatMessageConsumer());
		addHandler(new WorldDefinitionConsumer());
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
	
	public class WorldDefinitionConsumer extends MessageConsumer<WorldDefinitionMessage> {
		@Override
		public void consume(ChannelHandlerContext ctx, WorldDefinitionMessage message) {
			logger().debug("New World Definition = {}", message.world().getName());
			client().setWorld(message.world());
		}
	}
	
	public class TileMessageConsumer extends MessageConsumer<BasicTileMessage> {

		@Override
		public void consume(ChannelHandlerContext ctx, BasicTileMessage message) {
			PlayerSession session = PlayerSession.findSession(ctx);
			if(message.isGroup()) {
				logger().warn("Unhandled group " + TileMessageConsumer.class.getSimpleName());
			} else {
				logger().trace("Set Tile {} = {}", message.bottomLeft, message.data);
				client().world().setTile(message.bottomLeft, message.data);
			}
			
		}
		
	}
	
}
