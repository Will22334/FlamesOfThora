package com.thora.core.entity;

import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public class TypedWorldEntity extends AbstractWorldEntity {
	
	private EntityType type;
	
	protected TypedWorldEntity(final int id, final String name, final EntityType type, final Location loc) {
		super(id, name, loc);
		this.type = type;
	}
	
	public TypedWorldEntity(final String name, final EntityType type, final Location loc) {
		this(WorldEntity.EMPTY_ID, name, type, loc);
	}

	@Override
	public EntityType getEntityType() {
		return type;
	}
	
	public boolean setEntityType(final EntityType type) {
		this.type = type;
		return true;
	}
	
}
