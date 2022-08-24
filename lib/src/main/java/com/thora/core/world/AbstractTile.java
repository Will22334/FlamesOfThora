package com.thora.core.world;

public abstract class AbstractTile extends Tile {
	
	Location point;
	TileType type;
	
	public AbstractTile(TileType type, Location point) {
		this.point = point;
		this.type = type;
	}
	
	@Override
	public Location getLocation() {
		return point;
	}
	
	@Override
	public TileType getType() {
		return type;
	}
	
}
