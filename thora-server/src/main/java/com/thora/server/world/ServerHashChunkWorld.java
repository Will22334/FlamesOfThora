package com.thora.server.world;

import java.util.stream.Stream;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.EntityMessage;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Locatable;
import com.thora.core.world.TileGenerator;
import com.thora.core.world.WorldEntity;
import com.thora.server.ServerPlayer;
import com.thora.server.netty.ClientSession;

public class ServerHashChunkWorld extends HashChunkWorld {
	
	public ServerHashChunkWorld(String name, Locatable origin, int chunkWidth, int chunkHeight, PooledEngine engine,
			TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, engine, generator);
	}
	
	@Override
	protected boolean doRegister(final WorldEntity e) {
		e.setID(this.nextEntityID());
		getGeneratedChunk(e).addEntity(e);
		return true;
	}
	
	@Override
	protected boolean doDeRegister(final WorldEntity e) {
		getGeneratedChunk(e).removeEntity(e);
		e.setID(WorldEntity.EMPTY_ID);
		return true;
	}
	
	private static final ServerPlayer getPlayerFromEntity(final WorldEntity e) {
		return ((PlayerEntity) e).getPlayer();
	}
	
	public Stream<ServerPlayer> players(final HashChunk chunk) {
		return chunk.entities()
				.filter(PlayerEntity.class::isInstance)
				.map(ServerHashChunkWorld::getPlayerFromEntity);
	}
	
	public Stream<ServerPlayer> surroundingPlayers(final Locatable l) {
		return this.surroundingChunks(l, 1)
				.flatMap(this::players);
	}
	
	public void inform(final HashChunk chunk, final ServerPlayer player) {
		final ClientSession session = player.getSession();
		PlayerEntity entity = player.getEntity();
		session.write(BasicTileMessage.createRegion(chunk.bottomLeft.clone(), chunk.tiles));
		
		EntityMessage ents = new EntityMessage();
		chunk.entities()
		.filter(e -> !e.equals(entity))
		.forEach(ents::addCreate);
		
		if(!ents.isEmpty()) {
			session.write(ents);
		}
	}
	
	public void informSurroundingTiles(final Locatable center, final ServerPlayer player) {
		informSurroundingChunks(center, 1, player);
	}
	
	public void informSurroundingChunks(final Locatable center, final int chunkRange, final ServerPlayer player) {
		this.surroundingChunks(center, chunkRange)
		.forEach(c -> inform(c, player));
	}
	
	public BasicTileMessage getInformChunkMessage(final HashChunk chunk) {
		chunk.ensureGenerated();
		return BasicTileMessage.createRegion(chunk.bottomLeft.clone(), chunk.tiles);
	}
	
	@Override
	protected void onMoveEntity(final WorldEntity e, final HashChunk.CTile oldTile) {
		//TODO Inform nearby players/observers
	}
	
}
