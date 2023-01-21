package com.thora.client.net.netty;

import java.io.IOException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.Utils;
import com.thora.core.entity.EntityType;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.CameraEntityMessage;
import com.thora.core.net.message.CameraMessage;
import com.thora.core.net.message.CameraPointMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.EntityMessage;
import com.thora.core.net.message.EntityMoveRequestMessage;
import com.thora.core.net.message.LoginRequestMessage;
import com.thora.core.net.message.LoginResponseMessage;
import com.thora.core.net.message.StateChangeMessage;
import com.thora.core.net.message.WorldDefinitionMessage;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.ThoraCodec;
import com.thora.core.world.BasicTileData;
import com.thora.core.world.ClientHashChunkWorld;
import com.thora.core.world.Location;
import com.thora.core.world.Material;
import com.thora.core.world.TileData;
import com.thora.core.world.WeakVectorLocation;
import com.thora.core.world.World;
import com.thora.core.world.WorldEntity;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

public class ThoraClientCodec extends ThoraCodec {
	
	private final NettyNetworkManager manager;
	
	public ThoraClientCodec(NettyNetworkManager manager, Logger logger) {
		super(logger);
		this.manager = manager;
	}
	
	protected NettyNetworkManager manager() {
		return manager;
	}
	
	protected World world() {
		return manager().client().world();
	}
	
	@Override
	protected PlayerSession getSession(ChannelHandlerContext ctx) {
		return manager().getSession(ctx);
	}
	
	@Override
	protected void populate() {
		this.addEncoder(new LoginRequestEncoder());
		this.addEncoder(new ChatMessageEncoder());
		this.addEncoder(new EntityMoveRequestMessageEncoder());
		
		
		this.addDecoder(new LoginResponseDecoder());
		this.addDecoder(new ChatMessageDecoder());
		this.addDecoder(new WorldDefinitionDecoder());
		this.addDecoder(new TileMessageDecoder());
		this.addDecoder(new CameraMessageDecoder());
		this.addDecoder(new StateChangeMessageDecoder());
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
	
	public class EntityMessageDecoder extends MessageDecoder<EntityMessage> {
		
		public static final int HEADER_BIT_CREATE = 0x01;
		public static final int HEADER_BIT_UPDATE = 0x02;
		public static final int HEADER_BIT_DESTROY = 0x04;
		
		public EntityMessageDecoder() {
			super(OPCODE_CLIENT_ENTITY_INFORM);
		}
		@Override
		public EntityMessage decode(final ChannelHandlerContext ctx, final ByteBuf buf) throws IOException {
			final byte header = buf.readByte();
			
			IntObjectMap<WorldEntity> mapCreate = null;
			if((header & HEADER_BIT_CREATE) == HEADER_BIT_CREATE) {
				mapCreate = new IntObjectHashMap<>();
				EncodingUtils.decodIntMap(this::readEntityCreate, buf);
			}
			
			IntObjectMap<WorldEntity> mapUpdate = null;
			if((header & HEADER_BIT_CREATE) == HEADER_BIT_CREATE) {
				mapCreate = new IntObjectHashMap<>();
				EncodingUtils.decodIntMap(this::readEntityUpdate, buf);
			}
			
			return null;
		}
		
		private void readEntityCreate(final IntObjectMap<WorldEntity> map, final ByteBuf buf) {
			final int id = EncodingUtils.readPosVarInt(buf);
			final Location loc = ThoraCodec.read2DLocation(world(), buf);
			final EntityType type = EntityType.getAll().get(buf.readByte());
			final String name = EncodingUtils.readNullablerVarString(buf);
			
		}
		
		private void readEntityUpdate(final IntObjectMap<WorldEntity> map, final ByteBuf buf) {
			final int id = EncodingUtils.readPosVarInt(buf);
			final Location loc = ThoraCodec.read2DLocation(world(), buf);
			final WorldEntity entity = world().getEntity(id);
			
		}
		
	}
	
	public class StateChangeMessageDecoder extends MessageDecoder<StateChangeMessage> {
		public StateChangeMessageDecoder() {
			super(OPCODE_CLIENT_STATE_CHANGE);
		}
		@Override
		public StateChangeMessage decode(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
			return new StateChangeMessage(buf.readInt());
		}
	}
	
	public class CameraMessageDecoder extends MessageDecoder<CameraMessage> {
		public CameraMessageDecoder() {
			super(OPCODE_CLIENT_CAMERA_CHANGE);
		}
		@Override
		public CameraMessage decode(ChannelHandlerContext ctx, ByteBuf buf) throws IOException {
			final byte type = buf.readByte();
			final double scale = buf.readDouble();
			if(type == 0) {
				return new CameraPointMessage(read2DLocation(buf), scale);
			} else if(type == 1) {
				return new CameraEntityMessage(readEntityReference(buf), buf.readBoolean(), scale);
			}
			throw new IllegalStateException(String.format("Failed to decode %s due to invalid type code=%s, scale=%s", Utils.simpleClassName(CameraMessage.class), type, scale));
		}
	}
	
	public class EntityMoveRequestMessageEncoder extends MessageEncoder<EntityMoveRequestMessage> {
		protected EntityMoveRequestMessageEncoder() {
			super(OPCODE_CLIENT_MOVE_REQUSET);
		}
		@Override
		public void encode(final ChannelHandlerContext ctx, final EntityMoveRequestMessage msg, final ByteBuf buf) {
			ThoraCodec.write2DLocation(msg.getFrom(), buf);
			ThoraCodec.write2DLocation(msg.getTo(), buf);
		}
	}
	
	protected Location read2DLocation(final ByteBuf buf) {
		return new WeakVectorLocation<World>(world(), EncodingUtils.readSignedVarInt(buf), EncodingUtils.readSignedVarInt(buf));
	}
	
	protected WorldEntity readEntityReference(final ByteBuf buf) {
		return getEntity(EncodingUtils.readPosVarInt(buf));
	}
	
	protected WorldEntity getEntity(final int id) {
		return world().getEntity(id);
	}
	
	private static final TileData decodeTileData(final ByteBuf buf) {
		final Material mat = Material.get(buf.readByte());
		return new BasicTileData(mat);
	}
	
}
