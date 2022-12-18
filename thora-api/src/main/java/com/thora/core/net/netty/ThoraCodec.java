package com.thora.core.net.netty;

import org.apache.logging.log4j.Logger;

import com.thora.core.math.IntVector;
import com.thora.core.net.message.ThoraMessage;
import com.thora.core.world.Location;

import io.netty.buffer.ByteBuf;

public abstract class ThoraCodec extends PodCodec<ThoraMessage> {
	
	public static final int MAX_FRAME_SIZE = 5_000;
	
	public static final int OPCODE_SERVER_LOGIN_REQUEST = 0;
	
	public static final int OPCODE_CLIENT_LOGIN_RESPONSE = 1;
	
	public static final int OPCODE_SERVER_CHAT_MESSAGE = 2;
	public static final int OPCODE_CLIENT_CHAT_MESSAGE = 2;
	
	public static final int OPCODE_CLIENT_TILE_INFORM = 3;
	
	public ThoraCodec(Logger logger) {
		super(logger);
	}
	
	@Override
	protected abstract void populate();
	
	public ByteBuf writeIntVector(IntVector v, ByteBuf buf) {
		EncodingUtils.writeSignedVarInt(v.getIX(), buf);
		EncodingUtils.writeSignedVarInt(v.getIY(), buf);
		return buf;
	}
	
	public ByteBuf write2DLocation(Location point, ByteBuf buf) {
		EncodingUtils.writeSignedVarInt(point.getX(), buf);
		EncodingUtils.writeSignedVarInt(point.getY(), buf);
		return buf;
	}
	
}
