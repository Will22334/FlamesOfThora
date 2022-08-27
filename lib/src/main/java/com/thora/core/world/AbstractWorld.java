package com.thora.core.world;

import java.awt.Dimension;
import java.util.Objects;

public abstract class AbstractWorld extends World {
	
	protected String name;
	protected Dimension size;
	protected Pole origin;
	
	protected final TileGenerator generator;
	
	public AbstractWorld(String name, Dimension size, Locatable origin, TileGenerator generator) {
		this.name = Objects.requireNonNull(name, "World name cannot be null!");
		this.size = size;
		this.origin = new Pole("Origin", origin);
		this.generator = generator;
	}
	
	public AbstractWorld(String name, Dimension size, TileGenerator generator) {
		this(name, size, new Location(0,0), generator);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Dimension getSize() {
		return size;
	}
	
	@Override
	public Pole getOrigin() {
		return origin;
	}
	
	protected final TileType generate(Locatable loc) {
		return generator.getTileType(loc);
	}
	
	protected final TileType generate(int x, int y) {
		return generator.getTileType(x, y);
	}
	
	protected final void create() {
		Locatable origin = getOrigin();
		Locatable end = getEndOrigin();
		for(int y=origin.getY(); y<end.getY(); ++y) {
			for(int x=origin.getX(); x<end.getX(); ++x) {
				this.setTile(generate(x, y), x, y);
			}
		}
	}
	
}
