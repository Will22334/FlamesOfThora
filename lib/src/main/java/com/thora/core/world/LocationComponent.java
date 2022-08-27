package com.thora.core.world;

import com.badlogic.ashley.core.Component;

public class LocationComponent implements Component, Locatable {
	
	private Location location;
	
	@Override
	public final Location getLocation() {
		return location;
	}
	
	public final int getX() {
		return location.getX();
	}

	public final int getY() {
		return location.getY();
	}

	public final LocationComponent setLocation(Location location) {
		this.location = location;
		return this;
	}
	
	public LocationComponent setLocation(int x, int y) {
		return setLocation(new Location(x, y));
	}
	
}
