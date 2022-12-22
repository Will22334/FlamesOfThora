package com.thora.core.world;

import com.badlogic.ashley.core.PooledEngine;

public class ClientHashChunkWorld extends HashChunkWorld {
	
	public class ClientHashChunk extends HashChunk {

		protected ClientHashChunk(ChunkCoordinate coord) {
			super(coord);
		}
		
		protected ClientHashChunk generate() {
			for(int y=0; y<chunkHeight; ++y) {
				for(int x=0; x<chunkWidth; ++x) {
					Location point = getOrigin().clone().shift(x, y);
					tiles[y][x] = new CTile(Material.VOID, point);
				}
			}
			return this;
		}
		
	}
	
	public ClientHashChunkWorld(String name, ILocatable origin, int chunkWidth, int chunkHeight,
			PooledEngine engine, TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, engine, generator);
	}
	
	@Override
	protected Material generate(ILocatable loc) {
		return Material.VOID;
	}

	@Override
	protected Material generate(int x, int y) {
		return Material.VOID;
	}
	
	@Override
	protected boolean doRegister(IWorldEntity e) {
		this.getChunk(e).addEntity(e);
		this.entities.put(e.getID(), e);
		return true;
	}

	@Override
	protected boolean doDeRegister(IWorldEntity e) {
		this.entities.remove(e.getID());
		this.getChunk(e).removeEntity(e);
		e.setID(IWorldEntity.EMPTY_ID);
		return true;
	}
	
	
	
}
