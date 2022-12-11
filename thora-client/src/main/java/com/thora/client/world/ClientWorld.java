package com.thora.client.world;

import java.util.concurrent.ConcurrentHashMap;

import com.thora.core.world.KeyMapWorld;
import com.thora.core.world.Locatable;
import com.thora.core.world.TileGenerator;

public class ClientWorld extends KeyMapWorld {

	public ClientWorld(String name, Locatable origin) {
		super(new ConcurrentHashMap<>(), name, origin, TileGenerator.EMPTY);
	}
	
}
