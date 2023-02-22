package com.thora.core.net.message;

import com.thora.core.world.Locatable;
import com.thora.core.world.Location;

public class CameraPointMessage extends CameraMessage {
	
	private final Location focus;
	
	public CameraPointMessage(final Locatable focus, final double scale) {
		super(scale);
		this.focus = focus.getLocation();
	}
	
	@Override
	public Location getFocus() {
		return focus;
	}
	
}
