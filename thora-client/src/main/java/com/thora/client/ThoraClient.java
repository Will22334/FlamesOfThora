package com.thora.client;

import java.awt.Dimension;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Pole;
import com.thora.core.world.TileGenerator;
import com.thora.core.world.World;
import com.thora.core.world.generator.PerlinTileGenerator;

public class ThoraClient extends GameClient {
	
	protected static final int DEFAULT_VIEW_RANGE = FlamesOfThoraClient.DEFAULT_VIEW_RANGE;
	
	protected static final float DEFAULT_WORLD_SCALE = FlamesOfThoraClient.DEFAULT_WORLD_SCALE;
	protected static final float DEFAULT_WORLD_FREQ = FlamesOfThoraClient.DEFAULT_WORLD_FREQ;
	
	public static final Logger logger = LogManager.getLogger("Client");
	
	private PooledEngine engine = new PooledEngine();
	private World world;
	
	public PooledEngine engine() {
		return engine;
	}
	
	public World world() {
		return world;
	}
	
	@Override
	public void create() {
		TileGenerator gen = new PerlinTileGenerator((int)System.currentTimeMillis(), DEFAULT_WORLD_SCALE, DEFAULT_WORLD_FREQ);
		Dimension size = new Dimension(300,300);
		Pole origin = new Pole("Origin",0,0);
		
		//world = new KeyMapWorld(ConcurrentHashMap::new, "Earth", size, origin, gen);
		//world = new ArrayWorld("Earth", size, origin, 30, gen);
		world = new HashChunkWorld("Earth", origin, 15, 15, gen);
		
		logger().debug("World Backend: {} {}", world.getClass().getSimpleName(), world.getEstimatedArea());
	}
	
}
