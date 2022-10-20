package com.thora.core.net;

import io.netty.channel.socket.SocketChannel;
import io.netty.util.AttributeKey;

public class ThoraSession extends AbstractNettySession {
	
	public static final Long DEFAULT_SESSION_ID = -1L;
	
	public static final AttributeKey<Long> ATTRIBUTE_SESSION_ID = AttributeKey.newInstance("SESSION_ID");
	
	public ThoraSession(SocketChannel channel, long sessionID) {
		super(channel);
		Long prevID = attr(ATTRIBUTE_SESSION_ID).setIfAbsent(sessionID);
		if(prevID != null) {
			throw new IllegalStateException("Cannot ");
		}
	}
	
	public long getSessionID() {
		Long id = this.rawChannel().attr(ATTRIBUTE_SESSION_ID).get();
		if(id == null) return DEFAULT_SESSION_ID;
		return id.longValue();
	}
	
}
