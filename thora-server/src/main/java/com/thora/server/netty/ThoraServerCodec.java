package com.thora.server.netty;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.ThoraCodec;
import com.thora.server.ThoraServer;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ThoraServerCodec extends ThoraCodec {
	
	private final ThoraServer server;
	
	protected ThoraServer server() {
		return server;
	}
	
	public ThoraServerCodec(ThoraServer server, Logger logger) {
		super(logger);
		this.server = server;
	}
	
	@Override
	protected ClientSession getSession(ChannelHandlerContext ctx) {
		return ClientSession.get(ctx);
	}
	
	@Override
	protected void populate() {
		addDecoder(new LoginRequestDecoder());
		
		addEncoder(new LoginResponseEncoder());
	}
	
	public class LoginRequestDecoder extends MessageDecoder<LoginRequestMessage> {

		public LoginRequestDecoder() {
			super(ThoraCodec.OPCODE_SERVER_LOGIN_REQUEST);
		}

		@Override
		public LoginRequestMessage decode(ChannelHandlerContext ctx, ByteBuf encBuf) throws IOException {
			ClientSession session = getSession(ctx);
			if(!encBuf.isReadable()) {
				return null;
			}
			ByteBuf buf = ctx.alloc().buffer();
			try {
				EncodingUtils.decryptOther(buf, encBuf, session.getCryptoCreds().getAssymetric().getDecryptCipher());
				long timeStamp = buf.readLong();
				long sessionKey = buf.readLong();
				String username = EncodingUtils.readVarString(buf);
				String password = EncodingUtils.readVarString(buf);
				return new LoginRequestMessage(username, password, sessionKey, timeStamp);
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				throw new IOException(e);
			} finally {
				buf.release();
			}
		}
		
	}
	
	public class LoginResponseEncoder extends EncryptedPayloadMessageEncoder<LoginResponseMessage> {
		LoginResponseEncoder() {
			super(ThoraCodec.OPCODE_CLIENT_LOGIN_RESPONSE);
		}
		@Override
		public void encodePlain(ChannelHandlerContext ctx, LoginResponseMessage packet, ByteBuf buf) {
			buf.writeBoolean(packet.accepted);
			if(packet.reason != null) {
				EncodingUtils.writeVarString(packet.reason, buf);
			}
		}
	}
	
}
