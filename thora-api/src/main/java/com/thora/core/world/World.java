package com.thora.core.world;

import java.awt.Rectangle;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thora.core.Console;

/**
 * A general plane of {@link Tile Tiles} in which physical entities exist and operate.
 * For a class to implement {@link World} it has to define a rectangular region from {@link #getEstimatedArea()}, then
 *  implement {@link #getTile(int, int) and {@link #setTile(TileType, int, int).
 *  NOTE: these methods are the minimum required implementations and will most likely perform poorly without
 *   Implementing other methods.
 *
 */
public abstract class World implements Console {
	
	protected static final Rectangle noEstimatedSize() {
		return null;
	}
	
	public static IntStream reverseRange(int from, int to) {
		return IntStream.range(from, to)
				.map(i -> to - i + from - 1);
	}
	
	public static <T> Stream<T> reverseRange(T[] arr, int from, int to) {
		return reverseRange(from, to)
				.mapToObj(i -> arr[i]);
	}
	
	public static <T> Stream<T> reverseStream(T[] arr) {
		return reverseRange(arr, 0, arr.length);
	}
	
	public abstract String getName();
	
	public abstract Locatable getOrigin();
	
	public abstract void initialize() throws Exception;
	
	public abstract Rectangle getEstimatedArea();
	
	public String getEstimatedAreaString() {
		Rectangle rect = this.getEstimatedArea();
		if(rect != null) return rect.toString();
		return "[?,?]";
	}
	
	public Tile getTile(Locatable loc) {
		return getTile(loc.getLocation());
	}
	
	/**
	 * Returns the {@link Tile} located at the given (x,y) world coordinates.
	 * @param x the x world coordinates
	 * @param y the y world coordinates
	 * @return The tile at (x,y) or {@code null} if the tile does not currently exist.
	 */
	public abstract Tile getTile(int x, int y);
	
	public Tile getTile(Location l) {
		return getTile(l.getX(), l.getY());
	}
	
	public Tile setTile(TileType type, Location point) {
		return setTile(type, point.getX(), point.getY());
	}
	
	/**
	 * Sets the {@link TileType} for the {@link Tile} located at (x,y).
	 * @param type the new TyileType
	 * @param x the x world coordinates
	 * @param y the y world coordinates
	 * @return The current tile
	 */
	public abstract Tile setTile(TileType type, int x, int y);
	
	public Tile removeTile(int x, int y) {
		return setTile(TileType.VOID, x, y);
	}
	
	public abstract Stream<? extends Tile> tiles();
	
	public Map<Location,? extends Tile> getTiles() {
		return tiles()
				.collect(Collectors.toConcurrentMap(Tile::getLocation, Function.identity()));
	}
	
	public Stream<? extends Tile> surroundingTiles(Locatable center, int range) {
		Location point = center.getLocation();
		return tiles(point.getX() - range, point.getY() - range,
				point.getX() + range, point.getY() + range);
	}
	
	public Stream<Tile> surroundingTiles(int x, int y, int range) {
		return tiles(x-range, y-range, x+range, y+range);
	}
	
	public Stream<Tile> tiles(int minX, int minY, int maxX, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> tilesRow(y, minX, maxX))
				.flatMap(Function.identity())
				.filter(Objects::nonNull);
	}
	
	public Stream<Tile> tilesRow(int y, int minX, int maxX) {
		return IntStream.rangeClosed(minX, maxX)
				.mapToObj(x -> getTile(x,y));
	}
	
	public Stream<Tile> tilesColumn(int x, int minY, int maxY) {
		return IntStream.rangeClosed(minY, maxY)
				.mapToObj(y -> getTile(x,y));
	}
	
}
