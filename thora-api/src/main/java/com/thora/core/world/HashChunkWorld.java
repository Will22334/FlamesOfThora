package com.thora.core.world;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.math.IntVector;

public abstract class HashChunkWorld extends GeneralWorld {
	
	public class HashChunk extends Chunk {
		
		public class CTile extends BasicTile {
			protected CTile(Material material, Location point) {
				super(material, point);
			}
			@Override
			public HashChunkWorld getWorld() {
				return getChunk().getWorld();
			}
			protected HashChunk getChunk() {
				return HashChunk.this;
			}
			protected int ix() {
				return getX() - getOrigin().getX();
			}
			protected int iy() {
				return getY() - getOrigin().getY();
			}
		}
		
		
		protected final CTile[][] tiles;
		protected final Map<Integer,IWorldEntity> entities = new ConcurrentHashMap<>();
		protected final ChunkCoordinate coord;
		protected final Location bottomLeft;
		
		protected HashChunk(ChunkCoordinate coord) {
			this.coord = coord;
			this.bottomLeft = getChunkOrigin(coord);
			this.tiles = new CTile[chunkHeight][chunkWidth];
		}
		
		@Override
		public final HashChunkWorld getWorld() {
			return HashChunkWorld.this;
		}
		
		@Override
		public Location getOrigin() {
			return bottomLeft;
		}
		
		@Override
		public int getWidth() {
			return chunkWidth;
		}
		
		@Override
		public int getHeight() {
			return chunkHeight;
		}
		
		protected int ix(int wx) {
			return wx - bottomLeft.getX();
		}
		
		protected int ix(ILocatable loc) {
			return ix(loc.getX());
		}
		
		protected int iy(int wy) {
			return wy - bottomLeft.getY();
		}
		
		protected int iy(ILocatable loc) {
			return iy(loc.getY());
		}
		
		public CTile getTile(ILocatable loc) {
			return getTile(loc.getX(), loc.getY());
		}
		
		public CTile getTile(int wx, int wy) {
			return tiles[iy(wy)][ix(wx)];
		}
		
		public CTile setTile(Material type, int wx, int wy) {
			CTile tile = getTile(wx, wy);
			tile.data = new BasicTileData(type);
			return tile;
		}
		
		@Override
		public Stream<CTile> tiles() {
			return Arrays.stream(tiles)
					.flatMap(Arrays::stream);
		}
		
		
		protected boolean isGenerated() {
			return tiles[0][0] != null;
		}
		
		public HashChunk ensureGenerated() {
			return generate(false);
		}
		
		protected HashChunk generate(boolean overWrite) {
			if(!isGenerated() || overWrite) {
				generate();
			}
			return this;
		}
		
		protected HashChunk generate() {
			for(int y=0; y<chunkHeight; ++y) {
				for(int x=0; x<chunkWidth; ++x) {
					Location point = getOrigin().clone().shift(x, y);
					tiles[y][x] = new CTile(getWorld().generate(point), point);
				}
			}
			return this;
		}
		
		public Stream<? extends IWorldEntity> entities() {
			return entities.values().stream();
		}
		
		public void addEntity(IWorldEntity e) {
			getWorld().entities.put(e.getID(), e);
			this.entities.put(e.getID(), e);
		}
		
		public void removeEntity(IWorldEntity e) {
			this.entities.remove(e.getID());
			getWorld().entities.remove(e.getID());
		}
		
	}
	
	protected static class ChunkCoordinate extends IntVector {
		public ChunkCoordinate(int x, int y) {
			super(x, y);
		}
		
		@Override
		public ChunkCoordinate clone() {
			return new ChunkCoordinate(x, y);
		}
	}
	
	private static final Logger logger = LogManager.getLogger(HashChunkWorld.class);
	
	protected final int chunkWidth;
	protected final int chunkHeight;
	
	protected final Map<ChunkCoordinate,HashChunk> chunks = new HashMap<ChunkCoordinate,HashChunk>();
	protected final Map<Integer,IWorldEntity> entities = new ConcurrentHashMap<>();
	
	public HashChunkWorld(String name, ILocatable origin, int chunkWidth, int chunkHeight, PooledEngine engine, TileGenerator generator) {
		super(name, origin, engine, generator);
		
		this.chunkWidth = chunkWidth;
		this.chunkHeight = chunkHeight;
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	protected HashChunk getGeneratedChunk(int wx, int wy) {
		return getGeneratedChunk(getChunkCoord(wx, wy));
	}
	
	protected HashChunk getGeneratedChunk(ChunkCoordinate coord) {
		return chunks.computeIfAbsent(coord, this::createChunk)
				.ensureGenerated();
	}
	
	protected HashChunk getGeneratedChunk(ILocatable loc) {
		return getGeneratedChunk(loc.getX(), loc.getY());
	}
	
	protected HashChunk getChunk(ChunkCoordinate coord) {
		return chunks.get(coord);
	}
	
	protected HashChunk getChunk(ILocatable loc) {
		return getChunk(getChunkCoord(loc));
	}
	
	protected HashChunk getChunk(int wx, int wy) {
		return getChunk(getChunkCoord(wx, wy));
	}
	
	protected ChunkCoordinate getChunkCoord(int wx, int wy) {
		int cx = (int) Math.floor((wx+(chunkWidth-1)/2d)/chunkWidth);
		int cy = (int) Math.floor((wy+(chunkHeight-1)/2d)/chunkHeight);
		return new ChunkCoordinate(cx,cy);
	}
	
	protected ChunkCoordinate getChunkCoord(ILocatable loc) {
		return getChunkCoord(loc.getX(), loc.getY());
	}
	
	protected WeakVectorLocation<HashChunkWorld> getChunkOrigin(int cx, int cy) {
		return new WeakVectorLocation<>(this, chunkWidth * cx - chunkWidth/2,
				chunkHeight * cy - chunkHeight/2);
	}
	
	protected Location getChunkOrigin(ChunkCoordinate c) {
		return getChunkOrigin(c.getIX(), c.getIY());
	}
	
	protected HashChunk createGeneratedChunk(ChunkCoordinate coord) {
		return createChunk(coord).ensureGenerated();
	}
	
	protected HashChunk createChunk(ChunkCoordinate coord) {
		return new HashChunk(coord);
	}
	
	protected float chunkWidths(int dwx) {
		return 1f * dwx / chunkWidth;
	}
	
	protected float chunkHeights(int dwy) {
		return 1f * dwy / chunkHeight;
	}
	
	protected Stream<HashChunk> chunks() {
		return chunks.values().stream();
	}
	
	protected Stream<HashChunk> surroundingChunks(ILocatable p, int chunkRange) {
		HashChunk centerChunk = getGeneratedChunk(p);
		int cx = centerChunk.coord.getIX(), cy = centerChunk.coord.getIY();
		return IntStream.rangeClosed(cy-chunkRange, cy+chunkRange)
				.mapToObj(y -> {
					return IntStream.rangeClosed(cx-chunkRange, cx+chunkRange)
							.mapToObj(x -> getGeneratedChunk(new ChunkCoordinate(x,y)));
				})
				.flatMap(Function.identity());
	}
	
	@Override
	public Stream<HashChunk.CTile> tiles() {
		return chunks()
				.flatMap(HashChunk::tiles);
	}

	@Override
	public Stream<HashChunk.CTile> surroundingTiles(ILocatable center, int range) {
		return surroundingChunks(center, 1)
				.flatMap(HashChunk::tiles)
				.filter(t -> center.getWalkingDistance(t) <= range);
	}
	
	public Stream<HashChunk.CTile> surroundingTiles(ILocatable center) {
		return surroundingChunks(center, 1)
				.flatMap(HashChunk::tiles);
	}
	
	@Override
	public Tile getTile(int wx, int wy) {
		return getGeneratedChunk(wx, wy).getTile(wx, wy);
	}
	
	@Override
	public Tile getTile(Location p) {
		return getTile(p.getX(), p.getY());
	}
	
	@Override
	public Tile setTile(Material type, int wx, int wy) {
		return getGeneratedChunk(wx, wy).setTile(type, wx, wy);
	}
	
	@Override
	public Tile setTile(Material type, Location p) {
		return setTile(type, p.getX(), p.getY());
	}

	@Override
	public WeakVectorLocation<HashChunkWorld> getLocation(int x, int y) {
		return new WeakVectorLocation<HashChunkWorld>(this, x, y);
	}
	
	@Override
	public Stream<? extends IWorldEntity> entities() {
		return chunks()
				.flatMap(HashChunk::entities);
	}
	
	protected abstract boolean doRegister(IWorldEntity e);
	
	protected abstract boolean doDeRegister(IWorldEntity e);
	
}
