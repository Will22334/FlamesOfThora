package com.thora.server.world;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Locatable;
import com.thora.core.world.WorldEntity;
import com.thora.core.world.TileGenerator;

public class ServerHashChunkWorld extends HashChunkWorld {

	public ServerHashChunkWorld(String name, Locatable origin, int chunkWidth, int chunkHeight, PooledEngine engine,
			TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, engine, generator);
	}

	@Override
	protected boolean doRegister(WorldEntity e) {
		e.setID(this.nextEntityID());
		getGeneratedChunk(e).addEntity(e);
		return true;
	}

	@Override
	protected boolean doDeRegister(WorldEntity e) {
		getGeneratedChunk(e).removeEntity(e);
		e.setID(WorldEntity.EMPTY_ID);
		return true;
	}

}
