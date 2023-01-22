package com.thora.core.world;

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import com.thora.core.Player;
import com.thora.core.math.BasicIntVector;

/**
 * A general plane of {@link Tile Tiles} in which physical entities exist and operate.
 * For a class to implement {@link AbstractWorld} it has to define a rectangular region from {@link #getEstimatedArea()}, then
 *  implement {@link #getTile(int, int)} and setTile.
 *  NOTE: these methods are the minimum required implementations and will most likely perform poorly without
 *   Implementing other methods.
 *
 */
public abstract class AbstractWorld implements World {
	
	protected int nextEntityID = 0;

	protected synchronized int nextEntityID() {
		return nextEntityID++;
	}

	public AbstractWorld() {
		
	}

	@Override
	public abstract String getName();

	public abstract Locatable getOrigin();

	@Override
	public boolean register(WorldEntity e) {
		Objects.requireNonNull(e, "Cannot register null Entity to World");
		if(e.isRegistered()) {
			throw new IllegalArgumentException("Entity already registered: " + e + " in " + e.world());
		}
		if(!e.contains(this)) {
			throw new IllegalArgumentException("Cannot register ");
		}
		return doRegister(e);
	}

	protected abstract boolean doRegister(WorldEntity e);

	@Override
	public boolean deRegister(WorldEntity e) {
		Objects.requireNonNull(e, "Cannot deRegister null Entity in World");
		if(!e.isRegistered()) {
			throw new IllegalArgumentException("Cannot deRegister unregisterd Entitiy " + e);
		}
		return doDeRegister(e);
	}

	protected abstract boolean doDeRegister(WorldEntity e);

	public Location getLocation(BasicIntVector v) {
		return getLocation(v.getIX(), v.getIY());
	}

	public abstract Location getLocation(int x, int y);

	public abstract void initialize() throws Exception;
	
	@Override
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

	@Override
	public Tile getTile(Location l) {
		return getTile(l.getX(), l.getY());
	}

	public Tile setTile(TileData data, Tile tile) {
		tile.setTileData(data);
		return tile;
	}

	public Tile setTile(TileData data, Location l) {
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
	
	@Override
	public Stream<? extends Tile> surroundingTiles(Locatable center, double drange) {
		Location point = center.getLocation();
		int range = (int) Math.ceil(drange);
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

	@Override
	public Stream<? extends Player> players() {
		throw new RuntimeException("Not implemented yet!");
	}
	
}
