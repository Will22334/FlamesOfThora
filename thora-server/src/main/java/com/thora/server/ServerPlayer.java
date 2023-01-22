package com.thora.server;

import com.thora.core.Player;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.world.Location;
import com.thora.server.netty.ClientSession;
import com.thora.server.world.PlayerEntity;

import io.netty.channel.ChannelFuture;

public interface ServerPlayer extends Player {
	
	public ClientSession session();
	
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
		return session().write(msg);
	}
	
	public default ChannelFuture writeAndFlush(Object msg) {
		return session().writeAndFlush(msg);
	}

	@Override
	default void executeCommand(String commandText) {
		world().logger().debug("Command: {} executing \"{}\"", getName(), commandText);
		throw new RuntimeException("Not implemented yet!");
	}
	
	@Override
	default void sendMessage(ChatMessage message) {
		session().writeAndFlush(message);
	}
	
}
