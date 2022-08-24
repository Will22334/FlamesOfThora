package com.thora.core.world;

public abstract class Tile {
	
	public abstract World getWorld();
	
	public abstract Location getLocation();
	
	public abstract TileType getType();
	
}
