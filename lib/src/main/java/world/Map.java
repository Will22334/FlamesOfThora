package world;

import java.awt.Dimension;
import java.util.HashMap;

import location.Location;

public class Map {

	private String name = "Empty";
	private Dimension Mapsize = new Dimension(0,0);
	
	private Pole Origin = new Pole("Origin", 0, 0);
	private Pole InverseOrigin = new Pole("Inverse", 0 , 0);
	
	private HashMap<Location, Chunk> MapChunks = new HashMap<Location, Chunk>();
	
	public Map(String name, int Mapsize, int chunksize, int tilesize) {
		
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







	//Getters and Setters.

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

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
