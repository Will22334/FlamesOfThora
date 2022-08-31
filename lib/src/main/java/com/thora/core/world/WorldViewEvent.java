package com.thora.core.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pool.Poolable;

public class WorldViewEvent implements Iterable<Tile>, Poolable {
	
	public static final Pool<WorldViewEvent> POOL = new Pool<WorldViewEvent>() {
		@Override
		protected WorldViewEvent newObject() {
			return new WorldViewEvent(new ArrayList<>());
		}
	};
	
	private Collection<Tile> viewTiles;
	
	private WorldViewEvent(Collection<Tile> c) {
		this.viewTiles = c;
	}
	
	public Collection<Tile> getTiles() {
		return viewTiles;
	}
	
	@Override
	public Iterator<Tile> iterator() {
		return viewTiles.iterator();
	}
	
	public WorldViewEvent addTiles(Collection<Tile> tiles) {
		this.viewTiles.addAll(tiles);
		return this;
	}
	
	public WorldViewEvent addTiles(Stream<Tile> tiles) {
		tiles.forEach(viewTiles::add);
		return this;
	}
	
	public void free() {
		POOL.free(this);
	}
	
	@Override
	public void reset() {
		viewTiles.clear();
	}
	
}
