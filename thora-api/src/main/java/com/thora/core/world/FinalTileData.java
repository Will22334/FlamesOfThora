package com.thora.core.world;

public class FinalTileData extends AbstractTileData {
	
	private final Material material;
	
	protected FinalTileData(Material material) {
		this.material = material;
	}
	
	@Override
	public final Material material() {
		return material;
	}
	
}