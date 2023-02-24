package com.thora.core.net.netty;

import java.time.Instant;
import java.util.function.Function;

import org.apache.logging.log4j.Logger;

import com.thora.core.math.IntVector;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.world.Locatable;
import com.thora.core.world.Location;
import com.thora.core.world.WeakVectorLocation;
import com.thora.core.world.World;
import com.thora.core.world.WorldEntity;

import io.netty.buffer.ByteBuf;

public abstract class ThoraCodec extends PodCodec<ThoraMessage> {
	
	public static final int MAX_FRAME_SIZE = 10 * 1024;
	
	public static final int OPCODE_SERVER_LOGIN_REQUEST = 0;
	
	public static final int OPCODE_CLIENT_LOGIN_RESPONSE = 1;
	
	public static final int OPCODE_SERVER_CHAT_MESSAGE = 2;
	public static final int OPCODE_CLIENT_CHAT_MESSAGE = 2;
	
	public static final int OPCODE_CLIENT_WORLD_INFORM = 3;
	
	public static final int OPCODE_CLIENT_TILE_INFORM = 4;
	
	public static final int OPCODE_CLIENT_ENTITY_INFORM = 5;
	
	public static final int OPCODE_CLIENT_CAMERA_CHANGE = 6;
	
	public static final int OPCODE_CLIENT_STATE_CHANGE = 7;
	
	public static final int OPCODE_CLIENT_MOVE_REQUSET = 8;
	
	public ThoraCodec(Logger logger) {
		super(logger);
	}
	
	@Override
	protected abstract void populate();
	
	public static ByteBuf writeIntVector(final IntVector v, final ByteBuf buf) {
		EncodingUtils.writeSignedVarInt(v.getIX(), buf);
		EncodingUtils.writeSignedVarInt(v.getIY(), buf);
		return buf;
	}
	
	public static ByteBuf write2DLocation(final Locatable l, final ByteBuf buf) {
		return write2DLocation(l.getLocation(), buf);
	}
	
	public static ByteBuf write2DLocation(final Location point, final ByteBuf buf) {
		EncodingUtils.writeSignedVarInt(point.getX(), buf);
		EncodingUtils.writeSignedVarInt(point.getY(), buf);
		return buf;
	}
	
	public static Function<ByteBuf,Location> read2DLocationDecoder(final World world) {
		return (buf) -> read2DLocation(world, buf);
	}
	
	public static <W extends World> Location read2DLocation(final W world, final ByteBuf buf) {
		final int x = EncodingUtils.readSignedVarInt(buf);
		final int y = EncodingUtils.readSignedVarInt(buf);
		return new WeakVectorLocation<>(world, x, y);
	}
	
	public static ByteBuf writeEntityReference(final WorldEntity entity, ByteBuf buf) {
		return EncodingUtils.writePosVarInt(entity.getID(), buf);
	}
	
	public static ByteBuf writeInstantUTC(final Instant time, final ByteBuf buf) {
		return buf.writeLong(time.getEpochSecond());
	}
	
	public static Instant readInstantUTC(final ByteBuf buf) {
		return Instant.ofEpochSecond(buf.readLong());
	}
	
}
