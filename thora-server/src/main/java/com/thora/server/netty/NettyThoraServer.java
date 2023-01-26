package com.thora.server.netty;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.net.NetworkSession;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.ThoraCodec;
import com.thora.core.world.World;
import com.thora.server.ThoraServer;
import com.thora.server.world.ServerHashChunkWorld;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.ResourceLeakDetector.Level;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

public class NettyThoraServer extends ThoraServer {
	
	private static final Logger networkLogger = LogManager.getLogger(NettyThoraServer.class.getPackage().getName() + ".Network");
	
	private final PooledEngine engine;
	private final ServerHashChunkWorld world;
	
	protected ServerBootstrap bootstrap;
	protected EventLoopGroup bossGroup, childGroup;
	protected final int bossIOThreads;
	protected final int workerIOThreads;
	protected ServerSocketChannel listenChannel;
	
	private final Map<InetSocketAddress,ClientSession> sessions = new ConcurrentHashMap<>();
	
	public NettyThoraServer(KeyPair identity, int sideThreads, int bossIOThreads, int workerIOThreads,
			ServerHashChunkWorld world) {
		super(identity);
		this.bossIOThreads = bossIOThreads;
		this.workerIOThreads = workerIOThreads;
		this.engine = new PooledEngine();
		this.world = world;
	}
	
	public PooledEngine engine() {
		return engine;
	}
	
	protected Logger netLogger() {
		return networkLogger;
	}
	
	public final World world() {
		return world;
	}
	
	@Override
	public ServerHashChunkWorld getWorld() {
		return world;
	}
	
	@Override
	public InetSocketAddress getListenSocket() {
		if(listenChannel == null) return null;
		return listenChannel.localAddress();
	}
	
	@Override
	public synchronized void start(InetSocketAddress address) throws Exception {
		try {
			try {
				bind(address).sync();
			} catch (InterruptedException e) {
				throw e;
			}
		} catch(Throwable t) {
			logger().atWarn().withThrowable(t).log("Exception while engine starting/binding to {}", address);
			throw t;
		}
	}
	
	@Override
	public synchronized void stop() {
		if(status == Status.ON) {
			status = Status.STOPPING;
			logger().info("Server stopping");
			this.shutdownAndWaitGroups();
			status = Status.OFF;
			logger().info("Server stopped");
		}
	}
	
	protected boolean canBind() {
		return getStatus() == Status.OFF;
	}
	
	protected ChannelFuture bind(InetSocketAddress address) {
		this.status = Status.STARTING;
		
		try {
			bossGroup = new NioEventLoopGroup(bossIOThreads);
			childGroup = new NioEventLoopGroup(workerIOThreads);
			
			
			bootstrap = new ServerBootstrap();
			bootstrap.group(bossGroup, childGroup)
			.channel(NioServerSocketChannel.class)
			.option(ChannelOption.SO_BACKLOG, 100)
			.childOption(ChannelOption.SO_KEEPALIVE, true)
			.childHandler(channelInit);
			
			// Bind and start to accept incoming connections.
			return bootstrap.bind(address)
					.addListener((ChannelFuture f) -> {
						if(f.isSuccess()) {
							listenChannel = (ServerSocketChannel) f.channel();
							netLogger().info("Bound to {}", listenChannel.localAddress());
							status = Status.ON;
						} else {
							status = Status.STOPPING;
							netLogger().warn("Failed to bind to {}, shutting down!", address);
							bossGroup.shutdownGracefully();
							childGroup.shutdownGracefully();
							this.status = Status.OFF;
						}
					});
			
		} catch(Throwable t) {
			netLogger().atWarn().withThrowable(t).log("Issue while creating and binding bootstrap");
			shutdownAndWaitGroups();
			status = Status.OFF;
			throw t;
		}
	}
	
	protected void shutdownAndWaitGroups() {
		shutdownGroups().syncUninterruptibly();
	}
	
	protected Promise<Void> shutdownGroups() {
		Future<?> f1 = bossGroup.shutdownGracefully();
		Future<?> f2 = childGroup.shutdownGracefully();
		
		EventExecutor ex = new DefaultEventLoop();
		Promise<Void> promise = ex.newPromise();
		promise.addListener(f -> {
			bootstrap = null;
			childGroup = null;
			bossGroup = null;
			ex.shutdownGracefully();
		});
		PromiseCombiner fCombiner = new PromiseCombiner(ex);
		ex.execute(() -> {
			fCombiner.add(f1);
			fCombiner.add(f2);
			fCombiner.finish(promise);
		});
		
		return promise;
	}
	
	protected ClientSession getSession(ChannelHandlerContext ctx) {
		return getSession(ctx.channel());
	}
	
	protected ClientSession getSession(Channel channel) {
		return NetworkSession.findSession(channel);
	}
	
	@Sharable
	protected final class ChannelWatcher extends ChannelHandlerAdapter {
		public static final String CLOSE_MESSAGE_FORCE = "An existing connection was forcibly closed by the remote host";
		public static final String CLOSE_MESSAGE_RESET = "Connection reset";
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			//netLogger().trace("Exception end of pipeline: {} : {}", ctx, cause);
			if(isCasual(cause)) {
				//Forcibly closed by other end.
				ctx.close();
				return;
			}
			netLogger().atWarn().withThrowable(cause).log("Exception not handled at end of pipeline: {}", ctx);
		}
		public boolean isCasual(Throwable cause) {
			return CLOSE_MESSAGE_FORCE.equals(cause.getMessage())
					|| CLOSE_MESSAGE_RESET.equals(cause.getMessage());
		}
	}
	
	protected final class DefaultFrameDecoder extends LengthFieldBasedFrameDecoder {
		DefaultFrameDecoder() {
			super(ThoraCodec.MAX_FRAME_SIZE, 0, 4, 0, 4);
		}
	}
	
	protected final class DefaultFrameEncoder extends LengthFieldPrepender {
		DefaultFrameEncoder() {
			super(4, false);
		}
	}
	
	public class FullPacketEncryptor extends MessageToByteEncoder<ByteBuf> {
		
		@Override
		protected void encode(ChannelHandlerContext ctx, ByteBuf in, ByteBuf out) throws Exception {
			ClientSession session = getSession(ctx);
			EncodingUtils.encryptOther(in, out, session.getCryptoCreds().getSymmetric().getEncryptCipher());
		}
		
	}
	
	public class FullPacketDecryptor extends MessageToMessageDecoder<ByteBuf> {
		
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf encBuf, List<Object> out) throws Exception {
			ClientSession session = getSession(ctx);
			ByteBuf plain = ctx.alloc().buffer(encBuf.readableBytes());
			EncodingUtils.decryptOther(plain, encBuf, session.getCryptoCreds().getSymmetric().getDecryptCipher());
			out.add(plain);
		}
		
	}
	
	private final class DefaultChannelCloseListener implements GenericFutureListener<ChannelFuture> {
		@Override
		public void operationComplete(ChannelFuture f) throws Exception {
			netLogger().info("Disconnected: {}", f.channel());
		}
	}
	
	private final DefaultChannelCloseListener closeListener = new DefaultChannelCloseListener();
	
	public static final String PIPELINE_FRAME_ENCODER = "FRAME_ENCODER";
	public static final String PIPELINE_FRAME_DECODER = "FRAME_DECODER";
	public static final String PIPELINE_CODEC = "CODEC";
	public static final String PIPELINE_HANDLER = "HANDLER";
	
	private final ChannelInitializer<SocketChannel> channelInit = new DefaultChannelInitializer();
	
	private final class DefaultChannelInitializer extends ChannelInitializer<SocketChannel> {
		@Override
		protected void initChannel(SocketChannel channel) throws Exception {
			//Check if there was already a network session that was in reconnecting phase
			// Do not handle reconnecting sessions yet.
			
			//ch.closeFuture().addListener(f -> session.onClose());
			
			netLogger().info("Connected: {}", channel);
			
			channel.closeFuture().addListener(closeListener);
			
			ClientSession session = new ClientSession(channel, NettyThoraServer.this, identity());
			channel.attr(ClientSession.ATTRIBUTE_SESSION).set(session);
			
			netLogger().info("New session created for Channel {}", channel);
			sessions.put(channel.remoteAddress(), session);
			
			//init channel pipeline
			
			ChannelWatcher watcher = new ChannelWatcher();
			
			channel.pipeline()
			.addLast(watcher)
			.addLast(channel.eventLoop(), watcher)
			.addLast(PIPELINE_FRAME_ENCODER, new DefaultFrameEncoder())
			.addLast(PIPELINE_FRAME_DECODER, new DefaultFrameDecoder())
			.addLast(PIPELINE_CODEC, new ThoraServerCodec(NettyThoraServer.this, netLogger()))
			.addLast(PIPELINE_HANDLER, new ThoraPacketHandler(NettyThoraServer.this, netLogger()))
			.addLast(watcher);
			
		}
	}
	
}
