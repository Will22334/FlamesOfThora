package com.thora.core.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class LocationComponent implements Component, Locatable {
	
	public static final ComponentMapper<LocationComponent> MAPPER = ComponentMapper.getFor(LocationComponent.class);
	
	private Location location;
	
	@Override
	public final Location getLocation() {
		return location;
	}
	
	@Override
	public final int getX() {
		return location.getX();
	}
	
	@Override
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
