package com.thora.client.net.netty;

import java.net.InetSocketAddress;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.net.LoginTransaction;
import com.thora.core.net.NetworkSession;
import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.ThoraCodec;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPromise;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * Used for Client side network IO
 *
 */
public class NettyNetworkManager {
	
	public static long generateSessionID() {
		long id;
		do {
			id = secureRand.nextLong();
		} while(id == -1L);
		return id;
	}
	
	private static final Logger logger = LogManager.getLogger(NettyNetworkManager.class.getPackage().getName() + ".Network");
	private static final Random sessionRand = new SecureRandom();
	
	private static final Random secureRand = new SecureRandom();
	
	private final PublicKey serverIdentity;
	private final Cipher publicEncCipher;
	
	
	private Bootstrap bootstrap = null;
	protected EventLoopGroup workerGroup = null;
	protected final int workerIOThreads;
	protected EventLoop eventLoop;
	protected SocketChannel channel;
	private Promise<PlayerSession> sessionPromise;
	
	private PlayerSession session;
	protected Promise<LoginTransaction> loginPromise;
	protected LoginTransaction loginTransaction;
	
	public NettyNetworkManager(int workerIOThreads, PublicKey serverIdentity, Cipher publicEncCipher) {
		super();
		this.serverIdentity = serverIdentity;
		this.publicEncCipher = publicEncCipher;
		this.workerIOThreads = workerIOThreads;
	}
	
	public NettyNetworkManager(int workerIOThreads, PublicKey serverIdentity) {
		this(workerIOThreads, serverIdentity, EncodingUtils.generateCipher(serverIdentity));
	}
	
	public Logger logger() {
		return logger;
	}
	
	protected final PublicKey getServerIdentity() {
		return serverIdentity;
	}
	
	protected final Cipher getPublicCipher() {
		return publicEncCipher;
	}
	
	protected PlayerSession session() {
		return session;
	}
	
	protected PlayerSession getSession(ChannelHandlerContext ctx) {
		return getSession(ctx.channel());
	}
	
	protected PlayerSession getSession(Channel channel) {
		return PlayerSession.get(channel);
	}
	
	protected SocketChannel rawChannel() {
		return channel;
	}
	
	public ChannelFuture write(Object msg) {
		return rawChannel().write(msg);
	}
	
	public ChannelFuture connect(InetSocketAddress address) {
		this.workerGroup = new NioEventLoopGroup(workerIOThreads);
		bootstrap = new Bootstrap();
		bootstrap.group(workerGroup)
		.channel(NioSocketChannel.class)
		.handler(channelInit);
		
		logger().debug("Attempting to connect to {}", address);
		
		ChannelFuture connectFuture = bootstrap.connect(address);
		SocketChannel rawChannel = (SocketChannel) connectFuture.channel();
		eventLoop = rawChannel.eventLoop();
		ChannelPromise activePromise = rawChannel.newPromise();
		sessionPromise = eventLoop.newPromise();
		loginPromise = eventLoop.newPromise();
		
		connectFuture.addListener((ChannelFuture f) -> {
			if(f.isSuccess()) {
				logger().info("Successfully connected to {}", f.channel());
				activePromise.setSuccess();
			} else {
				logger().warn("Failed to connect to server[{}]", address);
				activePromise.setFailure(f.cause());
			}
		});
		
		return activePromise;
	}
	
	
	
	public Future<LoginTransaction> connectAndLogin(InetSocketAddress address, String username, String password) {
		ChannelFuture connectFuture = connect(address);
		
		connectFuture.addListener((ChannelFuture f) -> {
			if(f.isSuccess()) {
				final PlayerSession session = getOrCreateSession(f.channel());
				
				LoginTransaction t = new LoginTransaction();
				t.request = new LoginRequestMessage(username, password, session.getSessionID(), session.getSessionTimeStamp());
				
				loginTransaction = t;
				f.channel().writeAndFlush(loginTransaction.request).addListener((ChannelFuture lf) -> {
					if(lf.isSuccess()) {
						final PlayerSession session2 = PlayerSession.get(lf.channel());
						if(!session.rawChannel().equals(session2.rawChannel())) {
							logger().warn("Session handshake/login response was from a different channel!");
							loginPromise.setFailure(new RuntimeException("Session handshake/login response was from a different channel!"));
							return;
						}
						session2.generateSymmetricCipher(getServerIdentity(), t.request.sessionKey, t.request.timeStamp);
					} else {
						loginPromise.setFailure(lf.cause());
					}
				});
			}
		});
		
		return loginPromise;
	}
	
	protected PlayerSession getSession(SocketChannel channel) {
		return NetworkSession.findSession(channel);
	}
	
	protected PlayerSession getOrCreateSession(SocketChannel channel) {
		PlayerSession s = getSession(channel);
		if(s == null) {
			s = new PlayerSession(this, channel, secureRand.nextLong(), System.currentTimeMillis());
			session = s;
		}
		return s;
	}
	
	protected PlayerSession getOrCreateSession(Channel channel) {
		return getOrCreateSession((SocketChannel)channel);
	}
	
	@Sharable
	protected final class ChannelWatcher extends ChannelHandlerAdapter {
		public static final String CLOSE_MESSAGE_FORCE = "An existing connection was forcibly closed by the remote host";
		public static final String CLOSE_MESSAGE_RESET = "Connection reset";
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			logger().trace("Exception end of pipeline: {} : {}", ctx, cause);
			if(isCasual(cause)) {
				//Forcibly closed by other end.
				ctx.close();
				return;
			}
			logger().atWarn().withThrowable(cause).log("Exception thrown at end of pipeline {}", ctx);
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
			PlayerSession session = getSession(ctx);
			EncodingUtils.encryptOther(in, out, session.getCryptoCreds().getSymmetric().getEncryptCipher());
		}
		
	}
	
	public class FullPacketDecryptor extends MessageToMessageDecoder<ByteBuf> {
		
		@Override
		protected void decode(ChannelHandlerContext ctx, ByteBuf encBuf, List<Object> out) throws Exception {
			PlayerSession session = getSession(ctx);
			ByteBuf plain = ctx.alloc().buffer(encBuf.readableBytes());
			EncodingUtils.decryptOther(plain, encBuf, session.getCryptoCreds().getSymmetric().getDecryptCipher());
			out.add(plain);
		}
		
	}
	
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
			
			PlayerSession session = null;
			try {
				session = getOrCreateSession(channel);
				
				//Initialize channel pipeline
				channel.pipeline()
				.addLast(new ChannelWatcher())
				.addLast(PIPELINE_FRAME_DECODER, new DefaultFrameDecoder())
				.addLast(PIPELINE_FRAME_ENCODER, new DefaultFrameEncoder())
				.addLast(PIPELINE_CODEC, new ThoraClientCodec(NettyNetworkManager.this, logger()))
				.addLast(PIPELINE_HANDLER, new ThoraClientPacketHandler(NettyNetworkManager.this, logger()));
				
				sessionPromise.setSuccess(session);
			} catch(Throwable t) {
				sessionPromise.tryFailure(t);
				throw t;
			}
			
		}
	}
	
}
