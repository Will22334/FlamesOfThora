package com.thora.core.world;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Stream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.Utils;
import com.thora.core.world.DoubleArrLocation.DoubleArrRefLocation;
import com.thora.core.world.IntVectorLocation.IntVectorRefLocation;

public class QuadTreeWorld extends AbstractWorld {
	
	private static final Logger logger = LogManager.getLogger(QuadTreeWorld.class);
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	public static interface QuadTreeTile {
		
	}
	
	protected static class QuadTreeTileD extends AbstractTile<DoubleArrRefLocation<QuadTreeWorld>> implements QuadTreeTile {

		public QuadTreeTileD(Material material, DoubleArrRefLocation<QuadTreeWorld> point) {
			super(material, point);
		}


		public QuadTreeTileD(TileData data, DoubleArrRefLocation<QuadTreeWorld> point) {
			super(data, point);
		}
		
		public QuadTreeTileD(QuadTreeWorld world, TileData data, int x, int y) {
			this(data, new DoubleArrRefLocation<>(world, x, y));
		}
		
		public QuadTreeTileD(QuadTreeWorld world, Material material, int x, int y) {
			this(material, new DoubleArrRefLocation<>(world, x, y));
		}


		@Override
		public String toString() {
			return "" + getMaterial() + getLocation();
		}
		
		
		
	}
	
	protected static class QuadTreeTileI extends AbstractTile<IntVectorRefLocation<QuadTreeWorld>> implements QuadTreeTile {

		public QuadTreeTileI(Material material, IntVectorRefLocation<QuadTreeWorld> point) {
			super(material, point);
		}


		public QuadTreeTileI(TileData data, IntVectorRefLocation<QuadTreeWorld> point) {
			super(data, point);
		}
		
		public QuadTreeTileI(QuadTreeWorld world, TileData data, int x, int y) {
			this(data, new IntVectorRefLocation<>(world, x, y));
		}
		
		public QuadTreeTileI(QuadTreeWorld world, Material material, int x, int y) {
			this(material, new IntVectorRefLocation<>(world, x, y));
		}


		@Override
		public String toString() {
			return "" + getMaterial() + getLocation();
		}
		
		
		
	}
	
	private static class QuadTreeD {

		private final StreamQuadTree<QuadTreeTileD> tree = new StreamQuadTree<>();
		private WeakReference<QuadTreeWorld> worldRef;

		public QuadTreeD(QuadTreeWorld world) {
			this.worldRef = new WeakReference<QuadTreeWorld>(world);
		}

		public QuadTreeWorld getWorld() {
			return worldRef.get();
		}
		
		public Stream<QuadTreeTileD> tilesIter() {
			return tree.queryStreamIter();
		}
		
		public Stream<QuadTreeTileD> tiles() {
			return tree.tiles();
		}
		
		public void insert(QuadTreeTileD tile) {
			insert(tile, tile.getLocation().comps());
		}

		public void insert(QuadTreeTileD tile, double... position) {
			tree.insert(tile, position);
		}
		
		public void remove(QuadTreeTileD tile) {
			remove(tile, tile.getLocation().comps());
		}
		
		public boolean remove(QuadTreeTileD tile, double... position) {
			return tree.remove(tile, position);
		}
		
		public void move(QuadTreeTileD tile, double[] end) {
			move(tile, tile.getLocation().comps(), end);
		}
		
		public boolean move(QuadTreeTileD tile, double[] start, double[] end) {
			return tree.move(tile, start, end);
		}

		public List<QuadTreeTileD> query(double[]... parallelotope) {
			return tree.query(parallelotope);
		}

		public int getDimensions() {
			return tree.getDimensions();
		}

	}
	
	private static class QuadTreeI {

		private final StreamIntQuadTree<QuadTreeTileI> tree = new StreamIntQuadTree<>();
		private WeakReference<QuadTreeWorld> worldRef;

		public QuadTreeI(QuadTreeWorld world) {
			this.worldRef = new WeakReference<QuadTreeWorld>(world);
		}

		public QuadTreeWorld getWorld() {
			return worldRef.get();
		}
		
		public Stream<QuadTreeTileI> tilesIter() {
			return tree.queryStreamIter();
		}
		
		public Stream<QuadTreeTileI> tiles() {
			return tree.tiles();
		}
		
		public void insert(QuadTreeTileI tile) {
			insert(tile, tile.getLocation().v.comps());
		}

		public void insert(QuadTreeTileI tile, int... position) {
			tree.insert(tile, position);
		}
		
		public void remove(QuadTreeTileI tile) {
			remove(tile, tile.getLocation().v.comps());
		}
		
		public boolean remove(QuadTreeTileI tile, int... position) {
			return tree.remove(tile, position);
		}
		
		public void move(QuadTreeTileI tile, int[] end) {
			move(tile, tile.getLocation().v.comps(), end);
		}
		
		public boolean move(QuadTreeTileI tile, int[] start, int[] end) {
			return tree.move(tile, start, end);
		}

		public List<QuadTreeTileI> query(int[]... parallelotope) {
			return tree.query(parallelotope);
		}

		public int getDimensions() {
			return tree.getDimensions();
		}

	}
	
	private QuadTreeD tileTree;
	
	private final String name;
	private final IntVectorRefLocation<QuadTreeWorld> origin;
	
	private final Rectangle size;;
	private final TileGenerator generator;
	
	public QuadTreeWorld(String name, int width, int height, TileGenerator generator) {
		this.name = name;
		this.tileTree = new QuadTreeD(this);
		this.origin = new IntVectorRefLocation<>(this, 0, 0);
		this.size = new Rectangle(-width/2, -height/2, width, height);
		this.generator = generator;
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public Locatable getOrigin() {
		return origin;
	}

	@Override
	public IntVectorRefLocation<QuadTreeWorld> getLocation(int x, int y) {
		return new IntVectorRefLocation<QuadTreeWorld>(this, x, y);
	}

	@Override
	public void initialize() throws Exception {
		if(this.generator.isEmpty()) return;
		Rectangle rect = this.size;
		Location start = this.getLocation(rect.x, rect.y);
		Location end = start.clone().shift(rect.width, rect.height);
		
		Utils.Timer tm = new Utils.Timer();
		tm.start();
		
		for(int y=start.getY(); y<end.getY(); ++y) {
			for(int x=start.getX(); x<end.getX(); ++x) {
				this.setTile(generator.getTileType(x, y), x, y);
			}
		}
		
		long genDur = tm.mark();
		
		
		long size = this.tiles().count();
		long streamDur = tm.nextMark();
		logger().info("Took {}ms to generate {} tiles and stream through them in {}ms", genDur, size, streamDur, this);
		
	}

	@Override
	public QuadTreeTileD getTile(int x, int y) {
		return getTile(new double[] {x, y});
	}
	
	public QuadTreeTileD getTile(double[] comps) {
		List<QuadTreeTileD> tiles = tileTree.query(comps, comps);
		assert tiles.size() < 2 : "More than 1 Tile at " + comps;
		return tiles.get(0);
	}
	
	@Override
	public QuadTreeTileD setTile(Material material, int x, int y) {
		//logger().trace("Setting [{}x{}] to {}", x, y, material);
		QuadTreeTileD tile = new QuadTreeTileD(this, material, x, y);
		tileTree.insert(tile);
		return tile;
	}

	@Override
	public Stream<QuadTreeTileD> tiles() {
		return this.tileTree.tiles();
	}

	@Override
	public Stream<? extends WorldEntity> entities() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean register(WorldEntity e) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deRegister(WorldEntity e) {
		// TODO Auto-generated method stub
		return false;
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
	public Stream<? extends Tile> surroundingTiles(Locatable center) {
		throw new RuntimeException("Not implemented");
	}

	@Override
	public Tile setTile(Location point, TileData data) {
		return setTile(data.material(), point);
	}

}
