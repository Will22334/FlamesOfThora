package com.thora.server.world;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.ILocatable;
import com.thora.core.world.IWorldEntity;
import com.thora.core.world.TileGenerator;

public class ServerHashChunkWorld extends HashChunkWorld {

	public ServerHashChunkWorld(String name, ILocatable origin, int chunkWidth, int chunkHeight, PooledEngine engine,
			TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, engine, generator);
	}

	@Override
	protected boolean doRegister(IWorldEntity e) {
		e.setID(this.nextEntityID());
		getGeneratedChunk(e).addEntity(e);
		return true;
	}

	@Override
	protected boolean doDeRegister(IWorldEntity e) {
		getGeneratedChunk(e).removeEntity(e);
		e.setID(IWorldEntity.EMPTY_ID);
		return true;
	}

}
