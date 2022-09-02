package com.thora.core.world;

import java.awt.Rectangle;
import java.util.Collections;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class KeyMapWorld extends AbstractWorld {
	
	
	public class HTile extends AbstractTile {
		private HTile(TileType type, Location point) {
			super(type, point);
		}
		@Override
		public KeyMapWorld getWorld() {
			return KeyMapWorld.this;
		}
	}
	
	private Map<Location,HTile> safeTileMap;
	private Map<Location,HTile> tileMap;
	
	public KeyMapWorld(Supplier<Map<Location,HTile>> mapSupplier, String name, Locatable origin, TileGenerator generator) {
		super(name, origin, generator);
		tileMap = mapSupplier.get();
		safeTileMap = Collections.unmodifiableMap(tileMap);
		create();
	}
	
	public KeyMapWorld(Map<Location,HTile> tileMap, String name, Locatable origin, TileGenerator generator) {
		super(name, origin, generator);
		this.tileMap = tileMap;
		safeTileMap = Collections.unmodifiableMap(tileMap);
		create();
	}
	
	@Override
	public final Map<Location,HTile> getTiles() {
		return safeTileMap;
	}

	@Override
	public HTile getTile(int x, int y) {
		return getTile(new Location(x, y));
	}
	
	@Override
	public HTile getTile(Location point) {
		return tileMap.get(point);
	}
	
	@Override
	public HTile setTile(TileType type, Location point) {
		HTile tile = tileMap.get(point);
		if(tile != null) {
			tile.type = type;
		} else {
			tile = new HTile(type, point);
			tileMap.put(point, tile);
		}
		return tile;
	}
	
	@Override
	public HTile setTile(TileType type, int x, int y) {
		return setTile(type, new Location(x, y));
	}

	@Override
	protected Rectangle getSpawnRegion() {
		return new Rectangle(0,0,50,50);
	}

	@Override
	public Stream<HTile> tiles() {
		return tileMap.values().stream();
	}
	
}
