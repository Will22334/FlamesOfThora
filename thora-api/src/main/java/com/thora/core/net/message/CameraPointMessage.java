package com.thora.core.net.message;

import com.thora.core.world.Locatable;

public class CameraPointMessage extends CameraMessage {
	
	private final Locatable focus;
	
	public CameraPointMessage(final Locatable focus, final double scale) {
		super(scale);
		this.focus = focus.getLocation();
	}
	
	@Override
	public Locatable getFocus() {
		return focus;
	}
	
}
