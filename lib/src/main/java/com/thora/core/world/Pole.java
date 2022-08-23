package com.thora.core.world;

public class Pole {

	private String name;
	
	private Location loc;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLoc() {
		return loc;
	}

	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
	//A advanced Constructor
	public Pole(String name, Location loc) {
		
		this.name = name;
		this.loc = loc;
		
	}
	
	//A generic Constructor
	public Pole(String name, int x, int y) {
		
		setName(name);
		setLoc(new Location(x,y));
	}
	
}
