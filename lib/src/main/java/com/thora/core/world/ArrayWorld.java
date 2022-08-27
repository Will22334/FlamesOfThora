package com.thora.core.world;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A {@link World} implementation that is backed by a single 2d array of tiles.
 * @author Dave
 *
 */
public class ArrayWorld extends World {
	
	private class STile extends AbstractTile {
		public STile(TileType type, Location point) {
			super(type, point);
		}
		@Override
		public final ArrayWorld getWorld() {
			return ArrayWorld.this;
		}
	}
	
	private static final Logger logger = LogManager.getLogger(ArrayWorld.class);
	
	protected static final Logger logger() {
		return logger;
	}
	
	private String name;
	private Dimension mapSize;
	private TileGenerator gen;
	
	private Pole origin;
	private Pole inverseOrigin = new Pole("Inverse", 0 , 0);
	private Pole spawn;
	
	private STile[][] tiles;
	
	private int xOff, yOff;
	
	public ArrayWorld(String name, Dimension mapSize, Locatable origin, int tilesize, TileGenerator gen) {
		
		this.name = Objects.requireNonNull(name, "World name cannot be null!");
		this.mapSize = mapSize;
		this.origin = new Pole("Origin", origin.getLocation().clone());
		this.gen = gen;
		
		create();
		
	}
	
	public ArrayWorld(String name, Dimension mapSize, int originX, int originY, int tilesize, TileGenerator gen) {
		this(name, mapSize, new Location(originX, originY), tilesize, gen);
	}
	
	public ArrayWorld(String name, Dimension mapSize, int tilesize, TileGenerator gen) {
		this(name, mapSize, 0, 0, tilesize, gen);
	}
	
	public ArrayWorld(String name, int width, int height, int tilesize, TileGenerator gen) {
		this(name, new Dimension(width, height), tilesize, gen);
	}
	
	private void create() {
		
		int width = getSize().width;
		int height = getSize().height;
		
		tiles = new STile[width][height];
		
		int startX = origin.getLocation().getX();
		int startY = origin.getLocation().getY();
		
		xOff = -startX;
		yOff = -startY;
		
		int xEnd = startX + width;
		int yEnd = startY + height;
		
		for(int y=startY; y<yEnd; ++y) {
			
			for(int x=startX; x<xEnd; ++x) {
				
				TileType type = gen.getTileType(x, y);
				
				STile tile = setTile(type, x, y);
				logger().trace("NewTile: {}", tile);
				
			}
			
		}
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Dimension getSize() {
		return mapSize;
	}
	
	public int width() {
		return getSize().width;
	}
	
	public int height() {
		return getSize().height;
	}
	
	@Override
	public Pole getOrigin() {
		return origin;
	}
	
	protected final int ix(int wx) {
		return wx + xOff;
	}
	
	protected final int iy(int wy) {
		return wy + yOff;
	}
	
	private Stream<Tile> tilesInternal(int minX, int minY, int maxX, int maxY) {
		return Arrays.stream(tiles, minY, maxY + 1)
				.flatMap(row -> Arrays.stream(row, minX, maxX + 1));
	}
	
	@Override
	public Stream<Tile> surroundingTiles(Locatable cloc, int range) {
		Location center = cloc.getLocation();
		int minX = Math.max(0, ix(center.getX() - range));
		int maxX = Math.min(width() - 1, ix(center.getX() + range));
		int minY = Math.max(0, iy(center.getY() - range));
		int maxY = Math.min(height() - 1, iy(center.getY() + range));
		return tilesInternal(minX, minY, maxX, maxY);
	}
	
	@Override
	public final STile getTile(Location point) {
		return getTile(point.getX(), point.getY());
	}
	
	@Override
	public final STile getTile(int x, int y) {
		return getTileInternal(ix(x), iy(y));
	}
	
	protected final STile getTileInternal(int ix, int iy) {
		return tiles[iy][ix];
	}
	
	@Override
	public STile setTile(TileType type, Location point) {
		int ix = ix(point.getX()), iy = iy(point.getY());
		STile tile = getTileInternal(ix, iy);
		if(tile == null) {
			tile = new STile(type, point);
			tiles[iy][ix] = tile;
		} else {
			tile.type = type;
		}
		return tile;
	}
	
	@Override
	public STile setTile(TileType type, int wx, int wy) {
		int ix = ix(wx), iy = iy(wy);
		STile tile = getTileInternal(ix, iy);
		if(tile == null) {
			tile = new STile(type, new Location(wx, wy));
			tiles[iy][ix] = tile;
		} else {
			tile.type = type;
		}
		return tile;
	}
	
}
