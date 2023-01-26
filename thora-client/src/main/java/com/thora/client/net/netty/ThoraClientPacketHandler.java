package com.thora.client.net.netty;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import com.thora.client.FlamesOfThoraClient;
import com.thora.core.Utils;
import com.thora.core.net.LoginTransaction;
import com.thora.core.net.NetworkSession;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.CameraEntityMessage;
import com.thora.core.net.message.CameraPointMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.StateChangeMessage;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.net.message.WorldDefinitionMessage;
import com.thora.core.net.netty.PodHandler;
import com.thora.core.world.Location;
import com.thora.core.world.TileData;
import com.thora.core.world.World;

import io.netty.channel.ChannelHandlerContext;

public class ThoraClientPacketHandler extends PodHandler<ThoraMessage> {
	
	private final FlamesOfThoraClient client;
	private final NettyNetworkManager manager;
	
	protected ThoraClientPacketHandler(final NettyNetworkManager manager, final Logger logger) {
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
	
	protected World world() {
		return getManager().client().world();
	}
	
	@Override
	protected <P extends ThoraMessage> void dispatch(final ChannelHandlerContext ctx,
			final PodHandler<ThoraMessage>.MessageConsumer<P> consumer, final ThoraMessage message) {
//		logger().atLevel(Level.TRACE).log(() -> {
//			return new ParameterizedMessage("Dispatching off-thread {} from {}", prettyMessage(message), NetworkSession.findSession(ctx));
//		});
		client().addTask(() -> super.dispatch(ctx, consumer, message));
	}
	
	@Override
	protected <P extends ThoraMessage> void onHandlerFound(final ChannelHandlerContext ctx, final Class<P> messageClass,
			final ThoraMessage message) {
		
	}

	@Override
	protected void populate() {
		addHandler(new LoginResponseConsumer());
		addHandler(new ChatMessageConsumer());
		addHandler(new WorldDefinitionConsumer());
		addHandler(new TileMessageConsumer());
		addHandler(new CameraPointMessageConsumer());
		addHandler(new CameraEntityMessageConsumer());
		addHandler(new StateChangeMessageConsumer());
	}
	
	public class LoginResponseConsumer extends MessageConsumer<LoginResponseMessage> {
		
		@Override
		public void consume(ChannelHandlerContext ctx, LoginResponseMessage message) {
			LoginTransaction t = getManager().loginTransaction;
			t.response = message;
			getManager().loginPromise.setSuccess(t);
		}
		
	}
	
	public class ChatMessageConsumer extends MessageConsumer<ChatMessage> {
		@Override
		public void consume(final ChannelHandlerContext ctx, final ChatMessage message) {
			PlayerSession session = PlayerSession.findSession(ctx);
			
			logger().info("Got Message \"{}\" from {}", message.content, session);
			client().handleNewChatMessage(message);
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
				final World world = client().world();
				final Location bottomLeft = message.bottomLeft;
				final TileData[][] tiles = message.tiles;
				final int height = tiles.length, width = tiles[0].length;
				logger().trace("Set Tiles[{}-{}] = {}", message.bottomLeft, message.bottomLeft.clone().shift(width, height), tiles);
				for(int y=0; y<height; ++y) {
					for(int x=0; x<width; ++x) {
						world.setTile(bottomLeft.clone().shift(x, y), tiles[y][x]);
					}
				}
				
			} else {
				logger().trace("Set Tile {} = {}", message.bottomLeft, message.data);
				client().world().setTile(message.bottomLeft, message.data);
			}
			
		}
		
	}
	
	
	
	public class CameraPointMessageConsumer extends MessageConsumer<CameraPointMessage> {
		@Override
		public void consume(ChannelHandlerContext ctx, CameraPointMessage message) {
			logger().debug("Camera focused on {}", message.getFocus());
			client().setFocus(message.getFocus());
		}
	}
	
	public class CameraEntityMessageConsumer extends MessageConsumer<CameraEntityMessage> {
		@Override
		public void consume(ChannelHandlerContext ctx, CameraEntityMessage message) {
			logger().debug("Camera focused on {}", message.getFocus());
			client().setFocus(message.getFocus());
		}
	}
	
	public class StateChangeMessageConsumer extends MessageConsumer<StateChangeMessage> {
		@Override
		public void consume(ChannelHandlerContext ctx, StateChangeMessage message) {
			logger().debug("Recieved StateChange[{}]", message.stateID);
			client().addTask(() -> {
				client().States.setActiveState(message.stateID);
			});
		}
	}
	
}
