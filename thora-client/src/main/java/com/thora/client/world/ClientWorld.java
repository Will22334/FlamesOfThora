package com.thora.client.world;

import java.util.concurrent.ConcurrentHashMap;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.world.KeyMapWorld;
import com.thora.core.world.Locatable;
import com.thora.core.world.TileGenerator;

public class ClientWorld extends KeyMapWorld {

	public ClientWorld(String name, Locatable origin, PooledEngine engine) {
		super(new ConcurrentHashMap<>(), name, origin, engine, TileGenerator.EMPTY);
	}
	
}
