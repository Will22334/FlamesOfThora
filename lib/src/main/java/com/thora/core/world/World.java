package com.thora.core.world;

import java.awt.Dimension;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public abstract class World {
	
	public abstract String getName();
	
	public abstract Dimension getSize();
	
	public abstract Pole getOrigin();
	
	public abstract Stream<Tile> surroundingTiles(Location center, int range);
	
	public Stream<Tile> surroundingTiles(int x, int y, int range) {
		return tiles(x-range, y-range, x+range, y+range);
	}
	
	public Stream<Tile> tiles(int minX, int minY, int maxX, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> tilesRow(y, minX, maxX))
				.flatMap(Function.identity());
	}
	
	public Stream<Tile> tilesRow(int y, int minX, int maxX) {
		return IntStream.rangeClosed(minX, maxX)
				.mapToObj(x -> getTile(x,y));
	}
	
	public Stream<Tile> tilesColumn(int x, int minY, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> getTile(x,y));
	}
	
	public abstract Tile getTile(Location point);
	
	public Tile getTile(int x, int y) {
		return getTile(new Location(x, y));
	}
	
	public abstract Tile setTile(TileType type, Location point);
	
	public Tile setTile(TileType type, int x, int y) {
		return setTile(type, new Location(x, y));
	}
	
}
