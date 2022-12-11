package com.thora.core.world;

public interface Tile extends Locatable {
	
//	public abstract World getWorld();
//	
//	@Override
//	public abstract Location getLocation();
//	
//	public abstract TileType getType();
	
	public TileData getTileData();
	
	public void setTileData(TileData tileData);
	
	public default Material getMaterial() {
		return getTileData().material();
	}
	
	public void setMaterial(Material material);
	
}
