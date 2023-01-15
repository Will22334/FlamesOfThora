package com.thora.core.net.message;

import com.thora.core.world.Location;
import com.thora.core.world.WorldEntity;

public class EntityMoveRequestMessage extends AbstractEntityMessage {
	
	private final Location from, to;
	
	public EntityMoveRequestMessage(final WorldEntity entity, final Location from, final Location to) {
		super(entity);
		this.from = from;
		this.to = to;
	}

	public Location getFrom() {
		return from;
	}

	public Location getTo() {
		return to;
	}
	
}
