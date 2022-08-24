package com.thora.core.world;

import java.awt.Dimension;

public abstract class World {
	
	public abstract String getName();
	
	public abstract Dimension getSize();
	
	public abstract Pole getOrigin();
	
	public abstract Tile getTile(Location point);
	
	public Tile getTile(int x, int y) {
		return getTile(new Location(x, y));
	}
	
	public abstract Tile setTile(TileType type, Location point);
	
}
