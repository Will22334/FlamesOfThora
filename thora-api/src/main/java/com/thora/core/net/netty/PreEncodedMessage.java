package com.thora.core.net.netty;

import com.thora.core.net.message.ThoraMessage;

import io.netty.buffer.ByteBuf;

/**
 * A {@link ThoraMessage} that be either partially or fully pre-encoded into a {@link ByteBuf}
 * , to reduce re-encoding a message when sent multiple times.
 * @author Dave
 *
 */
public interface PreEncodedMessage extends ThoraMessage {
	
	public ByteBuf messageBuffer();
	
	public boolean isBufferEncoded();
	
	/**
	 * Attempts to encode the re-usable {@link ByteBuf} if not already encoded.
	 * @return true if the buffer was encoded successfully, false if the buffer was already encoded.
	 */
	public boolean preEncodeBuffer();
	
}
