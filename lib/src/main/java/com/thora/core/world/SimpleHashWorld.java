package com.thora.core.world;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class SimpleHashWorld extends World {
	
	private class STile extends AbstractTile {
		public STile(TileType type, Location point) {
			super(type, point);
		}
		@Override
		public final SimpleHashWorld getWorld() {
			return SimpleHashWorld.this;
		}
	}
	
	private String name = "Empty";
	private Dimension Mapsize = new Dimension(0,0);
	
	private Pole Origin = new Pole("Origin", 0, 0);
	private Pole InverseOrigin = new Pole("Inverse", 0 , 0);
	
	private Map<Location,STile> tiles = new HashMap<Location,STile>();
	
	public SimpleHashWorld(String name, int Mapsize, int tilesize) {
		
		this.name = name;
		this.Mapsize = new Dimension(Mapsize, Mapsize);
		
		create();
		
	}
	
	private void create() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Dimension getSize() {
		return Mapsize;
	}
	
	@Override
	public Pole getOrigin() {
		return Origin;
	}
	
	@Override
	public STile getTile(Location point) {
		return tiles.get(point);
	}
	
	@Override
	public STile setTile(TileType type, Location point) {
		STile tile = tiles.get(point);
		if(tile == null) {
			tiles.put(point, new STile(type, point));
		} else {
			tile.type = type;
		}
		return tile;
	}
	
}
