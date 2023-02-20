package com.thora.server.netty;

import java.time.Instant;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.thora.core.chat.Chat;
import com.thora.core.entity.EntityType;
import com.thora.core.net.message.CameraEntityMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.EntityMoveRequestMessage;
import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.StateChangeMessage;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.net.message.WorldDefinitionMessage;
import com.thora.core.net.netty.PodHandler;
import com.thora.core.world.Location;
import com.thora.core.world.WeakVectorLocation;
import com.thora.server.ServerPlayer;
import com.thora.server.world.PlayerEntity;
import com.thora.server.world.ServerHashChunkWorld;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;

public class ThoraPacketHandler extends PodHandler<ThoraMessage> {
	
	public static final Logger chatLogger() {
		return Chat.logger();
	}
	
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
		this.addHandler(new EntityMoveRequestMessageHandler());
	}
	
	private final class LoginRequestHandler extends MessageConsumer<LoginRequestMessage> {
		@Override
		public void consume(final ChannelHandlerContext ctx, final LoginRequestMessage message) {
			final ClientSession session = ClientSession.get(ctx);
			logger().atLevel(Level.TRACE).log("Received login request = {}", message);
			session.generateSymmetricCipher(server.publicKey(), message.sessionKey, message.timeStamp);
			
			final ServerHashChunkWorld w = server().getWorld();
			
			final ServerPlayer oldPlayer = w.getPlayer(message.username);
			if(oldPlayer != null) {
				LoginResponseMessage response = new LoginResponseMessage(false, String.format("User already logged in with username \"%s\"", message.username));
				session.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
				return;
			}
			
			LoginResponseMessage response = new LoginResponseMessage(true, "Successfully logged in!");
			ChannelFuture cf = session.rawChannel().write(response);
			if(response.isAccepted()) {
				
				final Location l = new WeakVectorLocation<>(w,0,0);
				final PlayerEntity p = new PlayerEntity(message.username, EntityType.HUMAN_MALE,  l);
				final NettyServerPlayer player = new NettyServerPlayer(session, message.username, p);
				p.setPlayer(player);
				session.setPlayer(player);
				
				
				w.register(p);
				
				session.rawChannel().closeFuture().addListener((ChannelFuture f) -> {
					if(session.getPlayer() != null) {
						player.world().deRegister(player.getEntity());
					}
					w.broadcastFlush(player.getUsername() + " logged out.");
				});
				
				
				session.write(new WorldDefinitionMessage(w));
				w.informSurroundingTiles(p, player);
				
				
				session.write(new CameraEntityMessage(p, true, 0f));
				session.write(new StateChangeMessage(3));
				
				w.broadcastFlush(player.getUsername() + " logged in.");
				
			}
		}
	}
	
	private final class ChatMessageHandler extends SessionMessageConsumer<ChatMessage,ClientSession> {
		@Override
		public void consume(final ChannelHandlerContext ctx, final ClientSession session, final ChatMessage packet) {
			
			final Instant time = Instant.now();
			final ServerPlayer player = session.getPlayer();
			
			
			
			if(packet.isCommand()) {
				//Handle command
				server().commandManager().executeCommand(player, packet.getContent());
				
				//TODO remove or refactor CommandSender.executeCommand(String)
				//player.executeCommand(packet.content);
				
			} else {
				
				//CHAT
				
				logger().info("{}: {}", player.getName(), packet.getContent());
				
				//chatLogger().info("{}:  {}", packet.getSenderName(), packet.getContent());
				final ServerHashChunkWorld w = (ServerHashChunkWorld) player.world();
				w.broadcastFlush(new ByteChatMessage(time, player, packet.getContent()));
			}
			
		}
	}
	
	private final class EntityMoveRequestMessageHandler extends MessageConsumer<EntityMoveRequestMessage> {
		
		@Override
		public void consume(ChannelHandlerContext ctx, EntityMoveRequestMessage message) {
			final ClientSession session = ClientSession.get(ctx);
			final ServerPlayer player = session.getPlayer();
			final PlayerEntity entity = player.getEntity();
			
			logger().trace("Handling MoveRequest from {} to {} for {}", message.getFrom(), message.getTo(), message.getEntity());
			entity.world().moveEntity(entity, message.getTo());
		}
		
	}
	
}
