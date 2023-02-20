package com.thora.core.entity;

import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public abstract class AbstractWorldEntity implements WorldEntity {
	
	private int id;
	protected String name;
	protected Location loc;
	
	public AbstractWorldEntity(final int id, final String name, final Location loc) {
		this.id = id;
		this.name = name;
		this.loc = loc;
	}
	
	public AbstractWorldEntity(final String name, final Location loc) {
		this(WorldEntity.EMPTY_ID, name,  loc);
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
	public String getName() {
		return name;
	}

	@Override
	public abstract EntityType getEntityType();
	
	public abstract boolean setEntityType(final EntityType type);
	
	@Override
	public void setLocation(final Location p) {
		this.loc = p;
	}
	
}
