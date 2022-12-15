package com.thora.core.world;

public interface TileData {
	
	public static final TileData VOID = new FinalTileData(Material.VOID);
	
	public Material material();
	
}
