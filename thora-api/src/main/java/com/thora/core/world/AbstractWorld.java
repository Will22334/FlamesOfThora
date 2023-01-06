package com.thora.core.world;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thora.core.math.IntVector;

/**
 * A general plane of {@link Tile Tiles} in which physical entities exist and operate.
 * For a class to implement {@link AbstractWorld} it has to define a rectangular region from {@link #getEstimatedArea()}, then
 *  implement {@link #getTile(int, int)} and setTile.
 *  NOTE: these methods are the minimum required implementations and will most likely perform poorly without
 *   Implementing other methods.
 *
 */
public abstract class AbstractWorld implements World {

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

	protected int nextID = 0;

	protected synchronized int nextEntityID() {
		return nextID++;
	}

	public AbstractWorld() {

	}

	@Override
	public abstract String getName();

	public abstract Locatable getOrigin();

	@Override
	public boolean register(WorldEntity e) {
		Objects.requireNonNull(e, "Cannot register null Entity in " + this);
		if(e.isRegistered()) {
			throw new IllegalArgumentException("Entity already registered " + e + " in " + e.getWorld());
		}
		return doRegister(e);
	}

	protected abstract boolean doRegister(WorldEntity e);

	@Override
	public boolean deRegister(WorldEntity e) {
		Objects.requireNonNull(e, "Cannot deRegister null Entity in " + this);
		if(!e.isRegistered()) {
			throw new IllegalArgumentException("Cannot deRegister unregisterd Entitiy " + e);
		}
		return doDeRegister(e);
	}

	protected abstract boolean doDeRegister(WorldEntity e);



	public Location getLocation(IntVector v) {
		return getLocation(v.getIX(), v.getIY());
	}

	public abstract Location getLocation(int x, int y);

	public abstract void initialize() throws Exception;

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

	public Tile setTile(TileData data, Tile tile) {
		tile.setTileData(data);
		return tile;
	}

	public Tile setTile(TileData data, Locatable l) {
		return setTile(data, l.getTile());
	}

	public Tile setTile(Material material, Location point) {
		return setTile(material, point.getX(), point.getY());
	}

	/**
	 * Sets the {@link TileType} for the {@link Tile} located at (x,y).
	 * @param type the new TyileType
	 * @param x the x world coordinates
	 * @param y the y world coordinates
	 * @return The current tile
	 */
	public abstract Tile setTile(Material material, int x, int y);

	public Tile removeTile(int x, int y) {
		return setTile(Material.VOID, x, y);
	}

	@Override
	public abstract Stream<? extends Tile> tiles();

	public Map<Location,? extends Tile> getTiles() {
		return tiles()
				.collect(Collectors.toConcurrentMap(Tile::getLocation, Function.identity()));
	}

	@Override
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
