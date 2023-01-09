package com.thora.core.world;

public class FinalTileData implements TileData {
	
	private final Material material;
	
	protected FinalTileData(Material material) {
		this.material = material;
	}
	
	@Override
	public final Material material() {
		return material;
	}
	
	@Override
	public String toString() {
		return material().toString();
	}
	
}
