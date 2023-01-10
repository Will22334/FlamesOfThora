package com.thora.server.world;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Locatable;
import com.thora.core.world.TileGenerator;
import com.thora.core.world.WorldEntity;
import com.thora.server.netty.ClientSession;

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
	
	public void informSurroundingTiles(final Locatable center, final ClientSession session) {
		informSurroundingChunks(center, 1, session);
	}
	
	public void informSurroundingChunks(final Locatable center, final int chunkRange, final ClientSession session) {
		this.surroundingChunks(center, chunkRange)
		.map(this::getInformChunkMessage)
		.forEach(session::write);
	}
	
	public BasicTileMessage getInformChunkMessage(final HashChunk chunk) {
		chunk.ensureGenerated();
		return BasicTileMessage.createRegion(chunk.bottomLeft.clone(), chunk.tiles);
	}
	
}
