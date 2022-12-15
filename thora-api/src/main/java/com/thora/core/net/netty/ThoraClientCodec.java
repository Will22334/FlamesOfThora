package com.thora.core.net.netty;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.LoginResponseMessage;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

public class ThoraClientCodec extends ThoraCodec {
	
	private final NettyNetworkManager manager;
	
	public ThoraClientCodec(NettyNetworkManager manager, Logger logger) {
		super(logger);
		this.manager = manager;
	}
	
	protected NettyNetworkManager manager() {
		return manager;
	}
	
	@Override
	protected PlayerSession getSession(ChannelHandlerContext ctx) {
		return manager().getSession(ctx);
	}
	
	@Override
	protected void populate() {
		this.addEncoder(new LoginRequestEncoder());
		this.addEncoder(new ChatMessageEncoder());
		
		
		this.addDecoder(new LoginResponseDecoder());
		this.addDecoder(new ChatMessageDecoder());
	}
	
	public class LoginRequestEncoder extends MessageEncoder<LoginRequestMessage> {

		protected LoginRequestEncoder() {
			super(OPCODE_SERVER_LOGIN_REQUEST);
		}

		@Override
		public void encode(ChannelHandlerContext ctx, LoginRequestMessage msg, ByteBuf buf) {
			int startReadIndex = buf.readerIndex();
			int startWriteIndex = buf.writerIndex();
			buf.readerIndex(startWriteIndex);
			
			buf.writeLong(msg.sessionKey);
			buf.writeLong(msg.timeStamp);
			
			EncodingUtils.writeVarString(msg.username, buf);
			EncodingUtils.writeVarString(msg.password, buf);
			
			try {
				EncodingUtils.encryptSame(buf, manager.getPublicCipher());
				buf.readerIndex(startReadIndex);
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				throw new RuntimeException(e);
			}
		}
		
	}
	
	public class LoginResponseDecoder extends EncryptedPayloadMessageDecoder<LoginResponseMessage> {
		public LoginResponseDecoder() {
			super(ThoraCodec.OPCODE_CLIENT_LOGIN_RESPONSE);
		}
		@Override
		protected LoginResponseMessage decodePlain(ChannelHandlerContext ctx, ByteBuf buf) {
			boolean accepted = buf.readBoolean();
			String reason = null;
			if(buf.isReadable()) {
				reason = EncodingUtils.readVarString(buf);
			}
			return new LoginResponseMessage(accepted, reason);
		}
	}
	
	public class ChatMessageEncoder extends EncryptedPayloadMessageEncoder<ChatMessage> {
		protected ChatMessageEncoder() {
			super(OPCODE_SERVER_CHAT_MESSAGE);
		}

		@Override
		public void encodePlain(ChannelHandlerContext ctx, ChatMessage packet, ByteBuf buf) {
			EncodingUtils.writeVarString(packet.message, buf);
		}
	}
	
	public class ChatMessageDecoder extends EncryptedPayloadMessageDecoder<ChatMessage> {

		public ChatMessageDecoder() {
			super(OPCODE_CLIENT_CHAT_MESSAGE);
		}

		@Override
		protected ChatMessage decodePlain(ChannelHandlerContext ctx, ByteBuf buf) {
			String text = EncodingUtils.readVarString(buf);
			return new ChatMessage(text);
		}
		
	}
	
}
