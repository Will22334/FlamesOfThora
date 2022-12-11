package com.thora.core.world;

public class Pole implements Locatable {
	
	public static Pole as(String defaultName, Locatable l) {
		if(l == null) return null;
		if(l instanceof Pole) {
			return (Pole) l;
		} else {
			return new Pole(defaultName, l);
		}
	}
	
	private String name;
	private Location loc;
	
	//A advanced Constructor
	public Pole(String name, Locatable loc) {
		
		this.name = name;
		this.loc = loc.getLocation();
		
	}
	
	//A generic Constructor
	public Pole(String name, World world, int x, int y) {
		
		this(name, new WeakVectorLocation<>(world, x, y));
	}
	
	public Pole(String name, int x, int y) {
		
		this(name, null, x, y);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public Location getLocation() {
		return loc;
	}
	
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
}
