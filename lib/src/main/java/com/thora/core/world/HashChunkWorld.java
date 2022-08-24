package com.thora.core.world;

import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

public class HashChunkWorld extends World {
	
	private String name = "Empty";
	private Dimension Mapsize = new Dimension(0,0);
	
	private Pole Origin = new Pole("Origin", 0, 0);
	private Pole InverseOrigin = new Pole("Inverse", 0 , 0);
	
	private Map<Location,Chunk> MapChunks = new HashMap<Location,Chunk>();
	
	public HashChunkWorld(String name, int Mapsize, int chunksize, int tilesize) {
		
		setName(name);
		setSize(new Dimension(Mapsize, Mapsize));
		
		CalculateMapParameters();
		
		Create();
		
	}
	
	private void Create() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	private void CalculateMapParameters() {
		// TODO Auto-generated method stub
		
	}
	
	
	@Override
	public Tile getTile(Location point) {
		// TODO Auto-generated method stub
		throw new RuntimeException("getTile(Location) not implemented yet!");
	}
	
	@Override
	public Tile setTile(TileType type, Location point) {
		throw new RuntimeException("setTile(TileType,Location) not implemented yet!");
	}
	
	//Getters and Setters.
	@Override
	public Pole getOrigin() {
		return Origin;
	}
	
	public void setOrigin(Pole origin) {
		Origin = origin;
	}
	
	public Pole getInverseOrigin() {
		return InverseOrigin;
	}
	
	public void setInverseOrigin(Pole inverseOrigin) {
		InverseOrigin = inverseOrigin;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Dimension getSize() {
		return Mapsize;
	}
	
	public void setSize(Dimension size) {
		this.Mapsize = size;
	}
	
	public Dimension getMapsize() {
		return Mapsize;
	}
	
	public void setMapsize(Dimension mapsize) {
		Mapsize = mapsize;
	}
	
	
}
