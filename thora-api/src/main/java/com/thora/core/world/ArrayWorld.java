package com.thora.core.world;

import java.awt.Dimension;
import java.awt.Rectangle;
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
public class ArrayWorld extends World implements RectangularRegion {
	
	private class STile extends AbstractTile {
		protected final int ix, iy;
		public STile(TileType type, Location point) {
			super(type, point);
			ix = ix(point.getX());
			iy = iy(point.getY());
		}
		@Override
		public final ArrayWorld getWorld() {
			return ArrayWorld.this;
		}
	}
	
	private static final Logger logger = LogManager.getLogger(ArrayWorld.class);
	
	private final String name;
	private Dimension mapSize;
	private Pole spawn;
	private TileGenerator gen;
	
	private Pole origin;
	
	private STile[][] tiles;
	
	private int xOff, yOff;
	
	public ArrayWorld(String name, Dimension mapSize, Locatable origin, int tilesize, TileGenerator gen) {
		this.name = Objects.requireNonNull(name, "World name cannot be null!");
		this.mapSize = Objects.requireNonNull(mapSize, "Cannot create World with null mapSize!");
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
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	public void initialize() {
		
		int startX = origin.getX();
		int startY = origin.getY();
		
		Rectangle size = new Rectangle(startX, startY, mapSize.width, mapSize.height);
		int width = size.width;
		int height = size.height;
		
		tiles = new STile[width][height];
		
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
		
		spawn = new Pole("Spawn", xEnd-width/2, yEnd-height/2);
		
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public int width() {
		return tiles[0].length;
	}
	
	public int height() {
		return tiles.length;
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
	
	@Override
	public Stream<Location> points() {
		return tiles()
				.map(Locatable::getLocation);
	}
	
	@Override
	public Rectangle getRectRegion() {
		return new Rectangle(getOrigin().getX(), getOrigin().getY(), width(), height());
	}
	
	private Stream<Tile> tilesInternal(int minX, int minY, int maxX, int maxY) {
		return Arrays.stream(tiles, minY, maxY + 1)
				.flatMap(row -> Arrays.stream(row, minX, maxX + 1));
	}
	
	@Override
	public Stream<Tile> tiles(int minX, int minY, int maxX, int maxY) {
		minX = Math.max(0, ix(minX));
		minY = Math.max(0, iy(minY));
		maxX = Math.min(width() - 1, ix(maxX));
		maxY = Math.min(height() - 1, iy(maxY));
		return super.tiles(minX, minY, maxX, maxY);
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
	
	@Override
	public Rectangle getEstimatedArea() {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Stream<? extends Tile> tiles() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
