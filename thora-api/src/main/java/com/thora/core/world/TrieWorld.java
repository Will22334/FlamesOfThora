package com.thora.core.world;

import java.awt.Rectangle;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.stream.Stream;

import org.danilopianini.util.FlexibleQuadTree;
import org.danilopianini.util.SpatialIndex;

import com.thora.core.world.DoubleArrLocation.DoubleArrRefLocation;

public class TrieWorld extends World {
	
	protected static class TrieTile extends AbstractTile<DoubleArrLocation> {

		public TrieTile(Material material, DoubleArrLocation point) {
			super(material, point);
		}


		public TrieTile(TileData data, DoubleArrLocation point) {
			super(data, point);
		}


	}
	
	private static class QuadTree {

		private final SpatialIndex<TrieTile> tree = new FlexibleQuadTree<>();
		private WeakReference<TrieWorld> worldRef;

		public QuadTree(TrieWorld world) {
			this.worldRef = new WeakReference<TrieWorld>(world);
		}

		public TrieWorld getWorld() {
			return worldRef.get();
		}

		public void insert(TrieTile tile) {
			insert(tile, tile.getLocation().comps());
		}

		public void insert(TrieTile tile, double... position) {
			tree.insert(tile, position);
		}
		
		public void remove(TrieTile tile) {
			remove(tile, tile.getLocation().comps());
		}
		
		public boolean remove(TrieTile tile, double... position) {
			return tree.remove(tile, position);
		}
		
		public void move(TrieTile tile, double[] end) {
			move(tile, tile.getLocation().comps(), end);
		}
		
		public boolean move(TrieTile tile, double[] start, double[] end) {
			return tree.move(tile, start, end);
		}

		public List<TrieTile> query(double[]... parallelotope) {
			return tree.query(parallelotope);
		}

		public int getDimensions() {
			return tree.getDimensions();
		}

	}
	
	private QuadTree tree;
	
	private final String name;
	private final DoubleArrRefLocation<TrieWorld> origin;
	
	public TrieWorld(String name) {
		this.name = name;
		this.tree = new QuadTree(this);
		this.origin = new DoubleArrRefLocation<>(this, 0d, 0d);
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
	public DoubleArrRefLocation<TrieWorld> getLocation(int x, int y) {
		return new DoubleArrRefLocation<TrieWorld>(this, x, y);
	}

	@Override
	public void initialize() throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public Rectangle getEstimatedArea() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tile getTile(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tile setTile(Material material, int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Stream<? extends Tile> tiles() {
		// TODO Auto-generated method stub
		return null;
	}

}
