package com.thora.core.world;

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
			TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, null, generator);
	}
	
	@Override
	protected Material generate(Locatable loc) {
		return Material.VOID;
	}

	@Override
	protected Material generate(int x, int y) {
		return Material.VOID;
	}
	
	
	
}
