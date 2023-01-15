package com.thora.core.net.message;

import com.thora.core.world.WorldEntity;

public class CameraEntityMessage extends CameraMessage {
	
	private final WorldEntity entity;
	private final boolean control;
	
	public CameraEntityMessage(final WorldEntity entity, final boolean control, final double scale) {
		super(scale);
		this.entity = entity;
		this.control = control;
	}

	@Override
	public WorldEntity getFocus() {
		return entity;
	}
	
	public boolean canControl() {
		return control;
	}
	
}
