package com.thora.server.netty;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.net.netty.PodHandler;
import com.thora.core.world.Location;
import com.thora.core.world.WeakVectorLocation;
import com.thora.core.world.World;
import com.thora.server.world.PlayerEntity;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;

public class ThoraPacketHandler extends PodHandler<ThoraMessage> {
	
	protected final NettyThoraServer server;
	
	ThoraPacketHandler(NettyThoraServer server, Logger logger) {
		super(logger);
		this.server = server;
	}
	
	protected final NettyThoraServer server() {
		return server;
	}
	
	@Override
	protected void populate() {
		this.addHandler(new LoginRequestHandler());
		this.addHandler(new ChatMessageHandler());
	}
	
	private final class LoginRequestHandler extends MessageConsumer<LoginRequestMessage> {
		@Override
		public void consume(ChannelHandlerContext ctx, LoginRequestMessage message) {
			ClientSession session = ClientSession.get(ctx);
			logger().atLevel(Level.TRACE).log("Received login request = {}", message);
			session.generateSymmetricCipher(server.publicKey(), message.sessionKey, message.timeStamp);
			
			LoginResponseMessage response = new LoginResponseMessage(true, "Successfully logged in!");
			ChannelFuture cf = session.rawChannel().writeAndFlush(response);
			if(response.isAccepted()) {
				World w = server().getWorld();
				Location l = new WeakVectorLocation<>(w,0,0);
				PlayerEntity p = new PlayerEntity(message.username, l);
				w.register(p);
				
				w.surroundingTiles(p)
				.forEach(t -> session.write(BasicTileMessage.createSingle(t)));
				session.rawChannel().flush();
				
			}
		}
	}
	
	private final class ChatMessageHandler extends MessageConsumer<ChatMessage> {
		@Override
		public void consume(ChannelHandlerContext ctx, ChatMessage message) {
			ClientSession session = ClientSession.get(ctx);
			logger().info("Got message \"{}\" from {}", message.message, session);
		}
	}
	
}
