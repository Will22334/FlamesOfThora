package com.thora.core.world;

public class TileRectangle implements RectangularRegion {
	
	private WorldRectangle rect;
	private Tile[][] tiles;
	
	TileRectangle(WorldRectangle rect) {
		this.rect = rect;
		tiles = new Tile[rect.getHeight()][rect.getWidth()];
	}
	
	@Override
	public World world() {
		return tiles[0][0].world();
	}

	@Override
	public WorldRectangle getRectRegion() {
		return rect;
	}
	
	void populate() {
		Location origin = rect.getBottomLeft().clone();
		tiles = new Tile[rect.getHeight()][rect.getWidth()];
		origin.world().putTiles2D(tiles, origin);
	}
	
}
