package com.thora.core.net.message;

import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public class EntityMoveRequestMessage extends AbstractThoraMessage {
	
	private final WorldEntity entity;
	private final Location from, to;
	
	public EntityMoveRequestMessage(WorldEntity entity, Location from, Location to) {
		this.entity = entity;
		this.from = from;
		this.to = to;
	}

	public WorldEntity getEntity() {
		return entity;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}
	
}
