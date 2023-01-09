package com.thora.client.net.netty;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.WorldDefinitionMessage;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.ThoraCodec;
import com.thora.core.world.BasicTileData;
import com.thora.core.world.ClientHashChunkWorld;
import com.thora.core.world.Location;
import com.thora.core.world.Material;
import com.thora.core.world.TileData;
import com.thora.core.world.World;

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
		this.addDecoder(new WorldDefinitionDecoder());
		this.addDecoder(new TileMessageDecoder());
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
			
			EncodingUtils.writeByteString(msg.username, buf);
			EncodingUtils.writeByteString(msg.password, buf);
			
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
			String reason = EncodingUtils.readString(buf);
			return new LoginResponseMessage(accepted, reason);
		}
	}
	
	public class ChatMessageEncoder extends EncryptedPayloadMessageEncoder<ChatMessage> {
		protected ChatMessageEncoder() {
			super(OPCODE_SERVER_CHAT_MESSAGE);
		}

		@Override
		public void encodePlain(ChannelHandlerContext ctx, ChatMessage packet, ByteBuf buf) {
			EncodingUtils.writeString(packet.message, buf);
		}
	}
	
	public class ChatMessageDecoder extends EncryptedPayloadMessageDecoder<ChatMessage> {
		public ChatMessageDecoder() {
			super(OPCODE_CLIENT_CHAT_MESSAGE);
		}
		@Override
		protected ChatMessage decodePlain(ChannelHandlerContext ctx, ByteBuf buf) {
			String text = EncodingUtils.readString(buf);
			return new ChatMessage(text);
		}
	}
	
	public class WorldDefinitionDecoder extends MessageDecoder<WorldDefinitionMessage> {
		public WorldDefinitionDecoder() {
			super(OPCODE_CLIENT_WORLD_INFORM);
		}
		@Override
		public WorldDefinitionMessage decode(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
			final String name = EncodingUtils.readString(buf);
			final World world = new ClientHashChunkWorld(name, 25, 25, new PooledEngine(), null);
			return new WorldDefinitionMessage(world);
		}
	}
	
	public class TileMessageDecoder extends MessageDecoder<BasicTileMessage> {

		protected TileMessageDecoder() {
			super(OPCODE_CLIENT_TILE_INFORM);
		}

		@Override
		public BasicTileMessage decode(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
			Location point = ThoraCodec.read2DLocation(null, buf);
			final boolean isGroup = buf.readBoolean();
			if(isGroup) {
				TileData[][] data = EncodingUtils.decode2DObjectArrayNoIndex(buf, TileData.class, ThoraClientCodec::decodeTileData);
				return BasicTileMessage.createRegion(point, data);
			} else {
				TileData data = decodeTileData(buf);
				return BasicTileMessage.createSingle(point, data);
			}
		}
		
	}
	
	private static final TileData decodeTileData(ByteBuf buf) {
		final Material mat = Material.get(buf.readByte());
		return new BasicTileData(mat);
	}
	
}
