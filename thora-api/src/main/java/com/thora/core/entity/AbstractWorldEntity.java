package com.thora.core.entity;

import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public abstract class AbstractWorldEntity implements WorldEntity {
	
	private int id;
	protected Location loc;
	
	public AbstractWorldEntity(final int id, final Location loc) {
		this.id = id;
		this.loc = loc;
	}
	
	@Override
	public Location getLocation() {
		return loc;
	}
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public AbstractWorldEntity setID(int id) {
		this.id = id;
		return this;
	}
	
	@Override
	public abstract EntityType getEntityType();
	
	@Override
	public void setLocation(Location p) {
		this.loc = p;
	}
	
}
