package com.thora.core.world;

import java.util.Objects;

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
	
	public ClientHashChunkWorld(String name, Locatable origin, int chunkWidth, int chunkHeight,
			PooledEngine engine, TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, engine, generator);
	}
	
	public ClientHashChunkWorld(String name, int chunkWidth, int chunkHeight,
			PooledEngine engine, TileGenerator generator) {
		super(name, chunkWidth, chunkHeight, engine, generator);
	}
	
	@Override
	protected Material generate(Locatable loc) {
		return Material.VOID;
	}

	@Override
	protected Material generate(int x, int y) {
		return Material.VOID;
	}
	
	@Override
	public boolean register(WorldEntity e) {
		Objects.requireNonNull(e, "Cannot register null WorldEntity to World!");
		return doRegister(e);
	}

	@Override
	public boolean deRegister(WorldEntity e) {
		Objects.requireNonNull(e, "Cannot deRegister null WorldEntity from World!");
		return doDeRegister(e);
	}

	@Override
	protected boolean doRegister(WorldEntity e) {
		HashChunk chunk = this.getChunk(e);
		if(chunk != null) {
			chunk.addEntity(e);
			return true;
		}
		return false;
	}

	@Override
	protected boolean doDeRegister(WorldEntity e) {
		HashChunk chunk = this.getChunk(e);
		if(chunk != null) {
			chunk.removeEntity(e);
			e.setID(WorldEntity.EMPTY_ID);
			return true;
		}
		return false;
	}
	
}
