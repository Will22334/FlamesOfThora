package com.thora.core.world;

public abstract class AbstractTile<L extends Location> implements Tile {

	protected L location;
	protected TileData data;

	public AbstractTile(TileData data, L point) {
		this.location = point;
		this.data = data;
	}

	public AbstractTile(Material material, L point) {
		this(new BasicTileData(material), point);
	}

	@Override
	public L getLocation() {
		return location;
	}

	@Override
	public TileData getTileData() {
		return data;
	}

	@Override
	public void setTileData(TileData tileData) {
		this.data = tileData;
	}

	@Override
	public void setMaterial(Material material) {
		this.data = new BasicTileData(material);
	}
	
}
