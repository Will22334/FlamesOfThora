package com.thora.server.world;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

import com.badlogic.ashley.core.PooledEngine;
import com.thora.core.net.message.BasicTileMessage;
import com.thora.core.net.message.ChatMessage;
import com.thora.core.net.message.EntityMessage;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Locatable;
import com.thora.core.world.TileGenerator;
import com.thora.core.world.WorldEntity;
import com.thora.server.ServerPlayer;
import com.thora.server.netty.ByteChatMessage;
import com.thora.server.netty.ClientSession;

public class ServerHashChunkWorld extends HashChunkWorld {
	
	private final Map<String,ServerPlayer> players = new ConcurrentHashMap<>();
	
	public ServerHashChunkWorld(String name, Locatable origin, int chunkWidth, int chunkHeight, PooledEngine engine,
			TileGenerator generator) {
		super(name, origin, chunkWidth, chunkHeight, engine, generator);
	}
	
	@Override
	protected boolean doRegister(final WorldEntity e) {
		e.setID(this.nextEntityID());
		getGeneratedChunk(e).addEntity(e);
		if(e instanceof PlayerEntity) {
			final PlayerEntity pE = (PlayerEntity) e;
			final ServerPlayer player = pE.getPlayer();
			this.players.put(player.getUsername(), player);
		}
		return true;
	}
	
	@Override
	protected boolean doDeRegister(final WorldEntity e) {
		getGeneratedChunk(e).removeEntity(e);
		e.setID(WorldEntity.EMPTY_ID);
		if(e instanceof PlayerEntity) {
			final PlayerEntity pE = (PlayerEntity) e;
			final ServerPlayer player = pE.getPlayer();
			this.players.remove(player.getUsername());
		}
		return true;
	}
	
	private static final ServerPlayer getPlayerFromEntity(final WorldEntity e) {
		return ((PlayerEntity) e).getPlayer();
	}
	
	public Stream<ServerPlayer> players() {
		return players.values().stream();
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
		final ClientSession session = player.session();
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
	
	public void broadcast(final ChatMessage message) {
		final ByteChatMessage m = ByteChatMessage.get(message);
		broadcast(m);
	}
	
	public void broadcast(final String message) {
		final ByteChatMessage m = new ByteChatMessage(message);
		m.preEncodeBuffer();
		players().forEach(p -> {
			m.messageBuffer().retain();
			p.write(m);
		});
	}
	
	public void broadcast(final ByteChatMessage m) {
		m.preEncodeBuffer();
		players().forEach(p -> {
			m.messageBuffer().retain();
			p.writeAndFlush(m);
		});
	}
	
	public void broadcastFlush(final ByteChatMessage m) {
		m.preEncodeBuffer();
		players().forEach(p -> {
			m.messageBuffer().retain();
			p.writeAndFlush(m);
		});
		m.messageBuffer().release();
	}
	
	public void broadcastFlush(final ChatMessage message) {
		final ByteChatMessage m = ByteChatMessage.get(message);
		broadcastFlush(m);
	}
	
	public void broadcastFlush(final String message) {
		final ByteChatMessage m = new ByteChatMessage(message);
		broadcastFlush(m);
	}
	
}
