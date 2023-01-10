package com.thora.core.world;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A {@link AbstractWorld} implementation that is backed by a single 2d array of tiles.
 * @author Dave
 *
 */
public class ArrayWorld extends AbstractWorld implements RectangularRegion {
	
	private class ATile extends BasicTile {
		protected final int ix, iy;
		public ATile(Material type, Location point) {
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
	
	private ATile[][] tiles;
	
	private int xOff, yOff;
	
	public ArrayWorld(String name, Dimension mapSize, Locatable origin, int tilesize, TileGenerator gen) {
		super();
		this.name = Objects.requireNonNull(name, "World name cannot be null!");
		this.mapSize = Objects.requireNonNull(mapSize, "Cannot create World with null mapSize!");
	}
	
	public ArrayWorld(String name, Dimension mapSize, int originX, int originY, int tilesize, TileGenerator gen) {
		this(name, mapSize, new WeakVectorLocation<>(null, originX, originY), tilesize, gen);
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
		
		tiles = new ATile[width][height];
		
		xOff = -startX;
		yOff = -startY;
		
		int xEnd = startX + width;
		int yEnd = startY + height;
		
		for(int y=startY; y<yEnd; ++y) {
			
			for(int x=startX; x<xEnd; ++x) {
				
				Material type = gen.getTileType(x, y);
				
				ATile tile = setTile(type, x, y);
				logger().trace("NewTile: {}", tile);
				
			}
			
		}
		
		spawn = new Pole("Spawn", this, xEnd-width/2, yEnd-height/2);
		
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
	public WorldRectangle getRectRegion() {
		return new WorldRectangle(getOrigin().getLocation(), width(), height());
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
	public final ATile getTile(Location point) {
		return getTile(point.getX(), point.getY());
	}
	
	@Override
	public final ATile getTile(int x, int y) {
		return getTileInternal(ix(x), iy(y));
	}
	
	protected final ATile getTileInternal(int ix, int iy) {
		return tiles[iy][ix];
	}
	
	@Override
	public ATile setTile(Material type, Location point) {
		int ix = ix(point.getX()), iy = iy(point.getY());
		ATile tile = getTileInternal(ix, iy);
		if(tile == null) {
			tile = new ATile(type, point);
			tiles[iy][ix] = tile;
		} else {
			tile.setMaterial(type);
		}
		return tile;
	}
	
	@Override
	public ATile setTile(Material type, int wx, int wy) {
		int ix = ix(wx), iy = iy(wy);
		ATile tile = getTileInternal(ix, iy);
		if(tile == null) {
			tile = new ATile(type, new WeakVectorLocation<>(this, wx, wy));
			tiles[iy][ix] = tile;
		} else {
			tile.setMaterial(type);
		}
		return tile;
	}
	
	@Override
	public ATile setTile(Location point, TileData data) {
		int ix = ix(point.getX()), iy = iy(point.getY());
		ATile tile = getTileInternal(ix, iy);
		if(tile == null) {
			tile = new ATile(data.material(), point);
			tiles[iy][ix] = tile;
		} else {
			tile.setMaterial(data.material());
		}
		return tile;
	}
	
	@Override
	public Stream<? extends Tile> tiles() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WeakVectorLocation<ArrayWorld> getLocation(int x, int y) {
		return new WeakVectorLocation<>(x, y);
	}

	@Override
	public Stream<? extends WorldEntity> entities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected boolean doRegister(WorldEntity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected boolean doDeRegister(WorldEntity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ArrayWorld getWorld() {
		return this;
	}

	@Override
	public Stream<? extends Tile> surroundingTiles(Locatable center) {
		throw new RuntimeException("Not implemented");
	}
	
}
