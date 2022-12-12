package com.thora.core.world;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.world.DoubleArrLocation.DoubleArrRefLocation;

public class QuadTreeWorld extends World {
	
	private static final Logger logger = LogManager.getLogger(QuadTreeWorld.class);
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	protected static class QuadTreeTile extends AbstractTile<DoubleArrRefLocation<QuadTreeWorld>> {

		public QuadTreeTile(Material material, DoubleArrRefLocation<QuadTreeWorld> point) {
			super(material, point);
		}


		public QuadTreeTile(TileData data, DoubleArrRefLocation<QuadTreeWorld> point) {
			super(data, point);
		}
		
		public QuadTreeTile(QuadTreeWorld world, TileData data, int x, int y) {
			this(data, new DoubleArrRefLocation<>(world, x, y));
		}
		
		public QuadTreeTile(QuadTreeWorld world, Material material, int x, int y) {
			this(material, new DoubleArrRefLocation<>(world, x, y));
		}


		@Override
		public String toString() {
			return "" + getMaterial() + getLocation();
		}
		
		
		
	}
	
	private static class QuadTree {

		private final StreamQueadTree<QuadTreeTile> tree = new StreamQueadTree<>();
		private WeakReference<QuadTreeWorld> worldRef;

		public QuadTree(QuadTreeWorld world) {
			this.worldRef = new WeakReference<QuadTreeWorld>(world);
		}

		public QuadTreeWorld getWorld() {
			return worldRef.get();
		}
		
		public Stream<QuadTreeTile> tilesIter() {
			return tree.queryStreamIter();
		}
		
		public Stream<QuadTreeTile> tiles() {
			return tree.tiles();
		}
		
		public void insert(QuadTreeTile tile) {
			insert(tile, tile.getLocation().comps());
		}

		public void insert(QuadTreeTile tile, double... position) {
			tree.insert(tile, position);
		}
		
		public void remove(QuadTreeTile tile) {
			remove(tile, tile.getLocation().comps());
		}
		
		public boolean remove(QuadTreeTile tile, double... position) {
			return tree.remove(tile, position);
		}
		
		public void move(QuadTreeTile tile, double[] end) {
			move(tile, tile.getLocation().comps(), end);
		}
		
		public boolean move(QuadTreeTile tile, double[] start, double[] end) {
			return tree.move(tile, start, end);
		}

		public List<QuadTreeTile> query(double[]... parallelotope) {
			return tree.query(parallelotope);
		}

		public int getDimensions() {
			return tree.getDimensions();
		}

	}
	
	private QuadTree tileTree;
	
	private final String name;
	private final DoubleArrRefLocation<QuadTreeWorld> origin;
	
	private final Rectangle size;;
	private final TileGenerator generator;
	
	public QuadTreeWorld(String name, int width, int height, TileGenerator generator) {
		this.name = name;
		this.tileTree = new QuadTree(this);
		this.origin = new DoubleArrRefLocation<>(this, 0d, 0d);
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
	public DoubleArrRefLocation<QuadTreeWorld> getLocation(int x, int y) {
		return new DoubleArrRefLocation<QuadTreeWorld>(this, x, y);
	}

	@Override
	public void initialize() throws Exception {
		if(this.generator.isEmpty()) return;
		Rectangle rect = this.size;
		Location start = this.getLocation(rect.x, rect.y);
		Location end = start.clone().shift(rect.width, rect.height);
		for(int y=start.getY(); y<end.getY(); ++y) {
			for(int x=start.getX(); x<end.getX(); ++x) {
				this.setTile(generator.getTileType(x, y), x, y);
			}
		}
		
	}

	@Override
	public Rectangle getEstimatedArea() {
		return null;
	}

	@Override
	public QuadTreeTile getTile(int x, int y) {
		return getTile(new double[] {x, y});
	}
	
	public QuadTreeTile getTile(double[] comps) {
		List<QuadTreeTile> tiles = tileTree.query(comps, comps);
		assert tiles.size() < 2 : "More than 1 Tile at " + comps;
		return tiles.get(0);
	}
	
	@Override
	public QuadTreeTile setTile(Material material, int x, int y) {
		logger().debug("Setting [{}x{}] to {}", x, y, material);
		QuadTreeTile tile = new QuadTreeTile(this, material, x, y);
		tileTree.insert(tile);
		return tile;
	}

	@Override
	public Stream<QuadTreeTile> tiles() {
		return this.tileTree.tiles();
	}

}
