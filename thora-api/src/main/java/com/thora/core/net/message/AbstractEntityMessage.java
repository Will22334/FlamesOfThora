package com.thora.core.net.message;

import com.thora.core.world.WorldEntity;

public abstract class AbstractEntityMessage extends AbstractThoraMessage {
	
	protected final WorldEntity entity;
	
	public AbstractEntityMessage(WorldEntity entity) {
		this.entity = entity;
	}
	
	public WorldEntity getEntity() {
		return entity;
	}
	
}
