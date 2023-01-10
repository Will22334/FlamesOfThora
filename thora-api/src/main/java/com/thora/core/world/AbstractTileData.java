package com.thora.core.world;

public abstract class AbstractTileData implements TileData {
	
	public AbstractTileData() {
		super();
	}
	
	@Override
	public abstract Material material();
	
	@Override
	public String toString() {
		return material().getName();
	}
	
}
