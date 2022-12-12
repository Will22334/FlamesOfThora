package com.thora.core.world;

public abstract class BasicTile implements Tile {
	
	Location point;
	protected TileData data;
	
	public BasicTile(TileData data, Location point) {
		this.point = point;
		this.data = data;
	}
	
	public BasicTile(Material material, Location point) {
		this(new BasicTileData(material), point);
	}
	
	@Override
	public Location getLocation() {
		return point;
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

	@Override
	public String toString() {
		return "[" + this.getMaterial() + ": " + getLocation() + "]";
	}
	
}