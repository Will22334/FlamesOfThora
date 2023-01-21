package com.thora.server.netty;

import com.thora.server.ServerPlayer;
import com.thora.server.world.PlayerEntity;

public class NettyServerPlayer implements ServerPlayer {
	
	private ClientSession session;
	private String userName;
	
	private PlayerEntity entity;
	
	public NettyServerPlayer(final ClientSession session, final String userName, final PlayerEntity entity) {
		this.session = session;
		this.userName = userName;
		this.entity = entity;
	}
	
	public NettyServerPlayer(final ClientSession session, final String userName) {
		this(session, userName, null);
	}
	
	@Override
	public ClientSession getSession() {
		return session;
	}
	
	@Override
	public String getUsername() {
		return userName;
	}
	
	@Override
	public PlayerEntity getEntity() {
		return entity;
	}
	
}
