package com.thora.core.world;

public class TileRectangle implements RectangularRegion {
	
	private WorldRectangle rect;
	private Tile[][] tiles;
	
	TileRectangle(WorldRectangle rect) {
		this.rect = rect;
		tiles = new Tile[rect.getHeight()][rect.getWidth()];
	}
	
	@Override
	public World getWorld() {
		return tiles[0][0].getWorld();
	}

	@Override
	public WorldRectangle getRectRegion() {
		return rect;
	}
	
	void populate() {
		Location origin = rect.getBottomLeft().clone();
		tiles = new Tile[rect.getHeight()][rect.getWidth()];
		origin.getWorld().putTiles2D(tiles, origin);
	}
	
}
