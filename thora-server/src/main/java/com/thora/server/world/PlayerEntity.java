package com.thora.server.world;

import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public class PlayerEntity implements WorldEntity {
	
	private int id;
	private String name;
	private Location loc;
	
	public PlayerEntity(String name, Location loc) {
		this.name = name;
		this.loc = loc;
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

}
