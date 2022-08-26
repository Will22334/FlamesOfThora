package com.thora.core.world;

import java.awt.Dimension;
import java.util.stream.Stream;

public abstract class World {
	
	public abstract String getName();
	
	public abstract Dimension getSize();
	
	public abstract Pole getOrigin();
	
	public abstract Stream<Tile> surroundingTiles(Location center, int range);
	
	public abstract Tile getTile(Location point);
	
	public Tile getTile(int x, int y) {
		return getTile(new Location(x, y));
	}
	
	public abstract Tile setTile(TileType type, Location point);
	
	public Tile setTile(TileType type, int x, int y) {
		return setTile(type, new Location(x, y));
	}
	
}
