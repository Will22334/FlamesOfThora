package com.thora.core.world;

import java.awt.Rectangle;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractWorld extends World {
	
	private static final Logger logger = LogManager.getLogger();
	
	protected String name;
	protected Pole origin;
	
	protected final TileGenerator generator;
	
	public AbstractWorld(String name, Locatable origin, TileGenerator generator) {
		this.name = Objects.requireNonNull(name, "World name cannot be null!");
		this.origin = new Pole("Origin", origin);
		this.generator = Objects.requireNonNull(generator, "World TileGenerator cannot be null!");
	}
	
	public AbstractWorld(String name, TileGenerator generator) {
		this(name, new Location(0,0), generator);
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public Pole getOrigin() {
		return origin;
	}
	
	@Override
	public Rectangle getEstimatedArea() {
		return World.noEstimatedSize();
	}

	protected abstract Rectangle getSpawnRegion();
	
	protected final TileType generate(Locatable loc) {
		return generator.getTileType(loc);
	}
	
	protected final TileType generate(int x, int y) {
		return generator.getTileType(x, y);
	}
	
	protected final void create() {
		Rectangle rect = getSpawnRegion();
		Location origin = new Location(rect.x, rect.y);
		Location end = origin.shift(rect.width, rect.height);
		for(int y=origin.getY(); y<end.getY(); ++y) {
			for(int x=origin.getX(); x<end.getX(); ++x) {
				this.setTile(generate(x, y), x, y);
			}
		}
	}
	
}
