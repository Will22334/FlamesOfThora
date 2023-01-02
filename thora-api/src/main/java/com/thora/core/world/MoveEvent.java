package com.thora.core.world;

import com.badlogic.ashley.core.Entity;

public class MoveEvent {
	
	public Location start, end;
	
	public MoveEvent(Location start, Location end) {
		super();
		this.start = start;
		this.end = end;
	}
	
	public MoveEvent(Entity entity, Locatable start) {
		this(start.getLocation(), LocationComponent.MAPPER.get(entity).getLocation());
	}
	
	public int dx() {
		return end.getX() - start.getX();
	}
	
	public int dy() {
		return end.getY() - start.getY();
	}
	
}
