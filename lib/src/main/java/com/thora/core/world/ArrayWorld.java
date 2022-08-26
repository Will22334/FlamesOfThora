package com.thora.core.world;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Random;
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
	
	private String name = "Empty";
	private Dimension mapSize = new Dimension(0,0);
	private TileGenerator gen;
	
	private Pole origin = new Pole("Origin", 0, 0);
	private Pole inverseOrigin = new Pole("Inverse", 0 , 0);
	
	private STile[][] tiles;
	
	private int xOff, yOff;
	
	
	public ArrayWorld(String name, int mapSize, int tilesize, TileGenerator gen) {
		
		this.name = name;
		this.mapSize = new Dimension(mapSize, mapSize);
		this.gen = gen;
		
		create();
		
	}
	
	private void create() {
		
		//Generate all tiles as grass where origin is center of tile rect.
		
		tiles = new STile[getSize().width][getSize().height];
		
		int xEnd = getSize().width / 2;
		int yEnd = getSize().height / 2;
		
		xOff = xEnd;
		yOff = yEnd;
		
		Random rand = new Random();
		
		for(int y=-yEnd; y<yEnd; ++y) {
			
			for(int x=-xEnd; x<xEnd; ++x) {
				
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
	public Stream<Tile> surroundingTiles(Location center, int range) {
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
			tiles[iy][ix] = new STile(type, point);
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
