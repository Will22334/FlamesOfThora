package com.thora.core.world;

import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thora.core.HasLogger;

public interface World extends HasLogger {
	
	public String getName();
	
	public default Tile getTile(Locatable loc) {
		return getTile(loc.getLocation());
	}
	
	public default Tile getTile(Location l) {
		return getTile(l.getX(), l.getY());
	}
	
	/**
	 * Returns the {@link Tile} located at the given (x,y) world coordinates.
	 * @param x the x world coordinates
	 * @param y the y world coordinates
	 * @return The tile at (x,y) or {@code null} if the tile does not currently exist.
	 */
	public Tile getTile(int x, int y);
	
	
	
	public Stream<? extends Tile> tiles();
	
	
	public default Stream<? extends Tile> surroundingWalkableTiles(Locatable l, double viewRange) {
		final Tile center = l.getTile();
		final Predicate<Tile> inRange = center.inWalkingRangePred(viewRange);
		
		return tiles()
				.filter(inRange);
	}
	
	public default Stream<? extends Tile> surroundingTiles(Locatable center, int range) {
		Location point = center.getLocation();
		return tiles(point.getX() - range, point.getY() - range,
				point.getX() + range, point.getY() + range);
	}
	
	public default Stream<Tile> surroundingTiles(int x, int y, int range) {
		return tiles(x-range, y-range, x+range, y+range);
	}
	
	public default Stream<Tile> tiles(int minX, int minY, int maxX, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> tilesRow(y, minX, maxX))
				.flatMap(Function.identity())
				.filter(Objects::nonNull);
	}
	
	public default Stream<Tile> tilesRow(int y, int minX, int maxX) {
		return IntStream.rangeClosed(minX, maxX)
				.mapToObj(x -> getTile(x,y));
	}
	
	public default Stream<Tile> tilesColumn(int x, int minY, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> getTile(x,y));
	}
	
	public default Tile[][] tiles2DArray(Locatable bottomLeft, int maxX, int maxY) {
		final Tile t = bottomLeft.getTile();
		int xr = maxX - bottomLeft.getX();
		int yr = maxY - bottomLeft.getY();
		Tile[][] tiles = new Tile[yr][xr];
		for(int y = bottomLeft.getY(); y<maxY; ++y) {
			for(int x = bottomLeft.getX(); x<maxX; ++x) {
				tiles[y][x] = getTile(x, y);
			}
		}
		
		return tiles;
	}
	
}
