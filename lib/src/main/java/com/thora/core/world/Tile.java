package com.thora.core.world;

public abstract class Tile implements Locatable {
	
	public abstract World getWorld();
	
	@Override
	public abstract Location getLocation();
	
	public abstract TileType getType();
	
}
