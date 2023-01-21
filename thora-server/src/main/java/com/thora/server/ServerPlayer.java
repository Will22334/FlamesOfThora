package com.thora.server;

import com.thora.core.Player;
import com.thora.core.world.Location;
import com.thora.server.netty.ClientSession;
import com.thora.server.world.PlayerEntity;

import io.netty.channel.ChannelFuture;

public interface ServerPlayer extends Player {
	
	public ClientSession getSession();
	
	public PlayerEntity getEntity();
	
	@Override
	public default String getUsername() {
		return getEntity().getName();
	}
	
	@Override
	public default Location getLocation() {
		return getEntity().getLocation();
	}
	
	public default ChannelFuture write(Object msg) {
		return getSession().write(msg);
	}
	
	public default ChannelFuture writeAndFlush(Object msg) {
		return getSession().writeAndFlush(msg);
	}
	
}
