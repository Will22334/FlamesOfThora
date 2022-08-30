package com.thora.core.world;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A general plane of {@link Tile Tiles} in which physical entities exist and operate.
 * For a class to implement {@link World} it has to define a rectangular region from {@link #getOrigin()} , {@link #getSize()} then
 *  implement {@link #getTile(int, int) and {@link #setTile(TileType, int, int).
 *  NOTE: these methods are the minimum required implementations and will most likely perform poorly without
 *   Implementing other methods.
 *
 */
public abstract class World implements RectangularRegion {
	
	public static Location[] getCorners(World world) {
		Location[] points = new  Location[2];
		points[0] = world.getOrigin().getLocation();
		points[1] = points[0].clone().shift(world.getSize().width, world.getSize().height);
		return points;
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
	
	public abstract Dimension getSize();
	
	public int getWidth() {
		return getSize().width;
	}
	
	public int getHeight() {
		return getSize().height;
	}
	
	public abstract Locatable getOrigin();
	
	public Locatable getEndOrigin() {
		return getOrigin().getLocation().clone().shift(getWidth(), getHeight());
	}
	
	@Override
	public Stream<Location> points() {
		return tiles()
				.map(Locatable::getLocation);
	}
	
	@Override
	public Rectangle getRectRegion() {
		return new Rectangle(getOrigin().getX(), getOrigin().getY(), getSize().width, getSize().height);
	}

	public Map<Location,? extends Tile> getTiles() {
		return tiles()
				.collect(Collectors.toConcurrentMap(Tile::getLocation, Function.identity()));
	}
	
	public Stream<Tile> tiles() {
		Location origin = getOrigin().getLocation();
		Dimension size = getSize();
		return tiles(origin.getX(), origin.getY(),
				origin.getX() + size.width - 1, origin.getY() + size.height - 1);
	}
	
	public Stream<Tile> surroundingTiles(Locatable center, int range) {
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
	
	public Tile getTile(Location point) {
		return getTile(point.getX(), point.getY());
	}
	
	/**
	 * Returns the {@link Tile} located at the given (x,y) world coordinates.
	 * @param x the x world coordinates
	 * @param y the y world coordinates
	 * @return The tile at (x,y),
	 * NULL if world coordinates are out of bounds.
	 */
	public abstract Tile getTile(int x, int y);
	
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
	
}
