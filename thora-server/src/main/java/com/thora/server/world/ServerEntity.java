package com.thora.server.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.thora.core.entity.EntityType;
import com.thora.core.entity.TypedWorldEntity;
import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public class ServerEntity extends TypedWorldEntity {
	
	private final Map<Class<?>,Object> components;
	
	protected ServerEntity(final int id, final String name, final EntityType type, final Location loc) {
		super(id, name, type, loc);
		this.components = new ConcurrentHashMap<>();
	}

	public ServerEntity(final String name, final EntityType type, final Location loc) {
		this(WorldEntity.EMPTY_ID, name, type, loc);
	}
	
	
	
}
