package com.thora.core.net.message;

import com.thora.core.world.Locatable;
import com.thora.core.world.Location;

public abstract class CameraMessage extends AbstractThoraMessage implements Locatable {
	
	protected final double scale;
	
	public CameraMessage(final double scale) {
		this.scale = scale;
	}
	
	public abstract Locatable getFocus();
	
	@Override
	public Location getLocation() {
		return getFocus().getLocation();
	}

	public double getScale() {
		return scale;
	}
	
}
