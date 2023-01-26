package com.thora.server.netty;

import java.time.Instant;

import com.thora.core.chat.Messageable;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.PreEncodedMessage;
import com.thora.core.net.netty.ThoraCodec;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;

public class ByteChatMessage extends ChatMessage implements PreEncodedMessage {
	
	public static final ByteBufAllocator DEFAULT_ALLOC = UnpooledByteBufAllocator.DEFAULT;
	
	public static final ByteChatMessage get(final ChatMessage message) {
		if(message instanceof ByteChatMessage) {
			return (ByteChatMessage) message;
		}
		if(message == null) {
			throw new NullPointerException("Cannot convert null ChattMessage into ByteChatMessage!");
		}
		return new ByteChatMessage(message.getTime(), message.getSender(), message.getContent(), DEFAULT_ALLOC);
	}
	
	protected ByteBuf messageBuf;
	private boolean encoded = false;
	
	protected ByteChatMessage(final Instant time, final Messageable sender, final String content, final ByteBuf buf) {
		super(time, sender, content);
		this.messageBuf = buf;
	}
	
	public ByteChatMessage(final Instant time, final Messageable sender, final String content, final ByteBufAllocator alloc) {
		this(time, sender, content, alloc.buffer());
	}
	
	public ByteChatMessage(final Instant time, final Messageable sender, final String content) {
		this(time, sender, content, DEFAULT_ALLOC);
	}
	
	public ByteChatMessage(final Messageable sender, final String content) {
		this(Instant.now(), sender, content, DEFAULT_ALLOC);
	}
	
	public ByteChatMessage(final String content) {
		this(Instant.now(), null, content, DEFAULT_ALLOC);
	}

	@Override
	public ByteBuf messageBuffer() {
		return messageBuf;
	}

	@Override
	public boolean isBufferEncoded() {
		return encoded;
	}

	@Override
	public boolean preEncodeBuffer() {
		if(encoded) {
			return false;
		}
		messageBuf = doPreEncode();
		encoded = true;
		return true;
	}
	
	protected ByteBuf doPreEncode() {
		final ByteBuf buffer = Unpooled.buffer();
		
		try {
			ThoraCodec.writeInstantUTC(getTime(), buffer);
			EncodingUtils.writenNullableVarString(getSenderName(), buffer);
			final String s = getContent();
			EncodingUtils.writeString(s, buffer);
		} catch (Throwable t) {
			t.printStackTrace();
			buffer.release();
		}
		
		return buffer;
	}
	
}
