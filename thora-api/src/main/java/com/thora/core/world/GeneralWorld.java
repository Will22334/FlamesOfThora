package com.thora.core.world;

import java.awt.Rectangle;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.world.TileGenerator.EmptyTileGenerator;

public abstract class GeneralWorld extends World {
	
	private static final Logger logger = LogManager.getLogger();
	
	protected String name;
	protected Pole origin;
	
	protected final TileGenerator generator;
	
	public GeneralWorld(String name, Pole origin, TileGenerator generator) {
		this.name = Objects.requireNonNull(name, "World name cannot be null!");
		this.origin = Objects.requireNonNull(origin, "World origin cannot be null!");
		if(generator == null) {
			generator = new EmptyTileGenerator();
		}
		this.generator = generator;
	}
	
	public GeneralWorld(String name, Locatable origin, TileGenerator generator) {
		this(name, Pole.as("origin", origin), generator);
	}
	
	public GeneralWorld(String name, TileGenerator generator) {
		this(name, new Pole("origin", 0,0), generator);
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
	
	protected Rectangle getSpawnRegion() {
		return getSpawnRegion(30,30);
	}
	
	protected Rectangle getSpawnRegion(int width, int height) {
		return new Rectangle(getOrigin().getX()-width/2, getOrigin().getY()-height/2, width, height);
	}
	
	protected final Material generate(Locatable loc) {
		return generator.getTileType(loc);
	}
	
	protected final Material generate(int x, int y) {
		return generator.getTileType(x, y);
	}
	
	@Override
	public void initialize() throws Exception {
		if(this.generator.isEmpty()) return;
		Rectangle rect = getSpawnRegion();
		Location end = origin.getLocation().shift(rect.width, rect.height);
		for(int y=origin.getY(); y<end.getY(); ++y) {
			for(int x=origin.getX(); x<end.getX(); ++x) {
				this.setTile(generate(x, y), x, y);
			}
		}
	}
	
}
