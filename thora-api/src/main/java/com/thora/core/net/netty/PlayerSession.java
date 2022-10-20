package com.thora.core.net.netty;

import java.security.PublicKey;
import java.util.Objects;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.AESKeyCipher;
import com.thora.core.net.AbstractNettySession;
import com.thora.core.net.AsymmetricKeyCipher;
import com.thora.core.net.NetworkSession;
import com.thora.core.net.SimpleCryptographicCredentials;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;

public class PlayerSession extends AbstractNettySession {
	
	//public static final AttributeKey<PlayerSession> ATTRIBUTE_SESSION = AttributeKey.newInstance("SESSION");
	
	public static final Long ABSENT_SESSION_ID = Long.valueOf(0L);
	
	public static PlayerSession get(ChannelHandlerContext ctx) {
		return get(ctx.channel());
	}
	
	public static PlayerSession get(Channel channel) {
		return NetworkSession.findSession(channel);
	}
	
	private final NettyNetworkManager manager;
	private SimpleCryptographicCredentials creds = new SimpleCryptographicCredentials();
	private final long sessionID;
	
	PlayerSession(NettyNetworkManager manager, NettyNetworkChannel channel, long sessionID) {
		super(channel);
		this.manager = manager;
		this.sessionID = sessionID;
		this.creds.assymetricKey = new AsymmetricKeyCipher(manager.getServerIdentity(), manager.getPublicCipher());
		SocketChannel rawChannel = channel.rawChannel();
		boolean set = rawChannel.attr(ATTRIBUTE_SESSION).compareAndSet(null, this);
		if(!set) {
			//Session already associated for the channel
			throw new IllegalStateException(getClass().getSimpleName() + " already associated with " + rawChannel);
		}
	}
	
	PlayerSession(NettyNetworkManager manager, SocketChannel channel, long sessionID) {
		this(manager, new NettyNetworkChannel(channel), sessionID);
	}
	
	protected NettyNetworkManager manager() {
		return manager;
	}
	
	protected ByteBufAllocator alloc() {
		return rawChannel().alloc();
	}
	
	public Logger logger() {
		return manager().logger();
	}
	
	@Override
	public SimpleCryptographicCredentials getCryptoCreds() {
		return creds;
	}
	
	public long getSessionID() {
		return sessionID;
	}
	
	protected boolean attach(SocketChannel channel) {
		boolean set = channel.attr(ATTRIBUTE_SESSION).compareAndSet(null, this);
		if(set) {
			
		} else {
			
		}
		return set;
	}
	
	protected void generateSymmetricCipher(PublicKey serverIdentity, long sessionID) {
		
		ByteBuf buf = alloc().buffer();
		try {
			buf.writeBytes(serverIdentity.getEncoded());
			buf.writeLong(sessionID);
			EncodingUtils.sha256(buf);
			byte[] keyData = new byte[buf.readableBytes()];
			buf.readBytes(keyData);
			
			SecretKey secret = new SecretKeySpec(keyData, "AES");
			creds.symmetricKey = new AESKeyCipher(secret);
		} catch(Exception e) {
			logger().atWarn().withThrowable(e).log("Exception thrown while generating symmetric session cipher!");
			throw e;
		} finally {
			buf.release();
		}
		
	}
	
	@Override
	public int hashCode() {
		return Objects.hashCode(getChannel());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof PlayerSession) {
			PlayerSession s = (PlayerSession) obj;
			return Objects.equals(getChannel(), s.getChannel());
		}
		return false;
	}
	
}
