package com.thora.server.netty;

import java.security.KeyPair;
import java.security.PublicKey;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.thora.core.net.AESKeyCipher;
import com.thora.core.net.AbstractNettySession;
import com.thora.core.net.AsymmetricKeyCipher;
import com.thora.core.net.SimpleCryptographicCredentials;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.NettyNetworkChannel;
import com.thora.server.ThoraServer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;

public class ClientSession extends AbstractNettySession {
	
	public static final AttributeKey<Long> ATTRIBUTE_SESSION_ID = AttributeKey.newInstance("SESSION_ID");
	
	public static ClientSession get(ChannelHandlerContext ctx) {
		return get(ctx.channel());
	}
	
	public static ClientSession get(Channel channel) {
		return (ClientSession) channel.attr(ClientSession.ATTRIBUTE_SESSION).get();
	}
	
	private final ThoraServer server;
	private SimpleCryptographicCredentials creds;
	
	protected ClientSession(NettyNetworkChannel channel, ThoraServer server, KeyPair pair) {
		super(channel);
		this.server = server;
		creds = new SimpleCryptographicCredentials();
		creds.assymetricKey = AsymmetricKeyCipher.createSafe(pair);
	}
	
	protected ClientSession(SocketChannel channel, ThoraServer server, KeyPair pair) {
		this(new NettyNetworkChannel(channel), server, pair);
	}
	
	public ThoraServer server() {
		return server;
	}
	
	protected ByteBufAllocator alloc() {
		return rawChannel().alloc();
	}
	
	@Override
	public SimpleCryptographicCredentials getCryptoCreds() {
		return creds;
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
			ThoraServer.globalLogger().atWarn().withThrowable(e)
			.log("Exception thrown while generating symmetric session cipher!");
			throw e;
		} finally {
			buf.release();
		}
		
	}
	
}
