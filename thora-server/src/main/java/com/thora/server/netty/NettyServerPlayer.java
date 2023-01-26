package com.thora.server.netty;

import com.google.common.base.Objects;
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
	public ClientSession session() {
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

	@Override
	public int hashCode() {
		return Objects.hashCode(session(), getUsername(), getEntity());
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof NettyServerPlayer) {
			NettyServerPlayer op = (NettyServerPlayer) obj;
			return Objects.equal(this.session(), op.session()) && Objects.equal(this.getUsername(), op.getUsername())
					&& Objects.equal(this.getEntity(), op.getEntity());
		}
		return false;
	}

	@Override
	public String toString() {
		return "Player:[" + getName() + "]";
	}
	
}
