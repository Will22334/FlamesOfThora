package com.thora.server.world;

import com.thora.core.Utils;
import com.thora.core.entity.EntityType;
import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;
import com.thora.server.ServerPlayer;

public class PlayerEntity implements WorldEntity {
	
	private ServerPlayer player;
	
	private int id = EMPTY_ID;
	private EntityType type;
	private String name;
	private Location loc;
	
	public PlayerEntity(final String name, final EntityType type, final Location loc) {
		this.name = name;
		this.type = type;
		this.loc = loc;
	}
	
	public ServerPlayer getPlayer() {
		return player;
	}
	
	public void setPlayer(ServerPlayer player) {
		this.player = player;
	}
	
	public String getName() {
		return name;
	}
	
	@Override
	public Location getLocation() {
		return loc;
	}
	
	@Override
	public void setLocation(Location p) {
		this.loc = p;
	}
	
	@Override
	public int getID() {
		return id;
	}

	@Override
	public PlayerEntity setID(int id) {
		this.id = id;
		return this;
	}
	
	@Override
	public EntityType getEntityType() {
		return type;
	}

	@Override
	public String toString() {
		return String.format("%s[%s, %s]", Utils.simpleClassName(this), getName(), getLocation());
	}
	
}
