package com.thora.core.world;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.badlogic.gdx.utils.Disposable;
import com.thora.core.HasLogger;
import com.thora.core.math.Vector;

public interface World extends HasWorld, Disposable, HasLogger {
	
	public String getName();
	
	@Override
	public default World getWorld() {
		return this;
	}
	
	public default Tile getTile(Locatable loc) {
		return getTile(loc.getLocation());
	}
	
	public default Tile getTile(Location point) {
		return getTile(point.getX(), point.getY());
	}
	
	/**
	 * Returns the {@link Tile} located at the given (x,y) world coordinates.
	 * @param x the x world coordinates
	 * @param y the y world coordinates
	 * @return The tile at (x,y) or {@code null} if the tile does not currently exist.
	 */
	public Tile getTile(int x, int y);
	
	public Stream<? extends Tile> tiles();
	
	
	public Tile setTile(Location point, TileData data);
	
	
	public Stream<? extends WorldEntity> entities();
	
	public boolean register(WorldEntity e);
	
	public boolean deRegister(WorldEntity e);
	
	public void moveEntity(WorldEntity e, Tile p);
	
	public default void moveEntity(WorldEntity e, Locatable l) {
		moveEntity(e, this.getTile(l));
	}
	
	public default void moveEntity(WorldEntity e, Vector v) {
		moveEntity(e, v.getIX(), v.getIY());
	}
	
	public default void moveEntity(WorldEntity e, int dx, int dy) {
		moveEntity(e, getTile(e.getLocation().getX() + dx, e.getLocation().getY() + dy));
	}
	
	public default Stream<? extends Tile> surroundingWalkableTiles(Locatable l, double viewRange) {
		final Tile center = l.getTile();
		final Predicate<Tile> inRange = center.inWalkingRangePred(viewRange);
		
		return tiles()
				.filter(inRange);
	}
	
	public Stream<? extends Tile> surroundingTiles(Locatable center);
	
	public default Stream<? extends Tile> surroundingTiles(Locatable center, int range) {
		Location point = center.getLocation();
		return tiles(point.getX() - range, point.getY() - range,
				point.getX() + range, point.getY() + range);
	}
	
	public default Stream<? extends Tile> surroundingTiles(Locatable center, double drange) {
		Location point = center.getLocation();
		int range = (int) Math.ceil(drange);
		return tiles(point.getX() - range, point.getY() - range,
				point.getX() + range, point.getY() + range)
				.filter(t -> t.isInRange(point, drange/2d));
	}
	
	public default Stream<Tile> surroundingTiles(int x, int y, int range) {
		return tiles(x-range, y-range, x+range, y+range);
	}
	
	public default Stream<Tile> tiles(int minX, int minY, int maxX, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> tilesRow(y, minX, maxX))
				.flatMap(Function.identity());
	}
	
	public default Stream<Tile> tilesRow(int y, int minX, int maxX) {
		return IntStream.rangeClosed(minX, maxX)
				.mapToObj(x -> getTile(x,y));
	}
	
	public default Stream<Tile> tilesColumn(int x, int minY, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> getTile(x,y));
	}
	
	public default Tile[][] tiles2DCentered(Locatable center, int xRange, int yRange) {
		final Location bottomLeft = center.getLocation().clone().shift(-xRange, -yRange);
		final Tile[][] tiles = new Tile[2*yRange + 1][2*xRange + 1];
		putTiles2D(tiles, bottomLeft);
		return tiles;
	}
	
	/**
	 * Returns a 2d Tile array that contains all tiles inside the rectangular region from bottomLeft to (maxX,maxY)
	 * @param bottomLeft The bottom left point of the region
	 * @param maxX the farthest x coordinate of the tile array
	 * @param maxY the farthest y coordinate of the tile array
	 * @return A newly constructed 2d Tile array containing all tile references in specified range.
	 */
	public default Tile[][] tiles2DArray(Locatable bottomLeft, int maxX, int maxY) {
		final Tile t = bottomLeft.getTile();
		int xr = maxX - bottomLeft.getX();
		int yr = maxY - bottomLeft.getY();
		Tile[][] tiles = new Tile[yr][xr];
		putTiles2D(tiles, t);
		return tiles;
	}
	
	public default void putTiles2D(Tile[][] tiles, Locatable bottomLeft) {
		final int height = tiles.length;
		final int width = tiles[0].length;
		final int maxX = bottomLeft.getX() + width;
		final int maxY = bottomLeft.getY() + height;
		for(int y = bottomLeft.getY(); y<maxY; ++y) {
			for(int x = bottomLeft.getX(); x<maxX; ++x) {
				tiles[y][x] = getTile(x, y);
			}
		}
	}
	
	@Override
	public default void dispose() {
		
	}
	
}
