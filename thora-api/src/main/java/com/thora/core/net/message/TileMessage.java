package com.thora.core.net.message;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import com.thora.core.world.Tile;

public class TileMessage extends AbstractThoraMessage {
	
	public static final TileMessage constructCreate(Tile tile) {
		return new TileMessage(tile, TileUpdateType.CREATE);
	}
	
	public static final TileMessage constructCreate(Tile[][] tiles) {
		return new TileMessage(tiles, TileUpdateType.CREATE);
	}
	
	public static final TileMessage constructDestroy(Tile tile) {
		return new TileMessage(tile, TileUpdateType.DESTROY);
	}
	
	public static final TileMessage constructDestroy(Tile[][] tiles) {
		return new TileMessage(tiles, TileUpdateType.DESTROY);
	}
	
	public static enum TileUpdateType {
		CREATE(),
		DESTROY();
		
		private static final TileUpdateType[] vals = TileUpdateType.values();
		private static final List<TileUpdateType> all = Collections.unmodifiableList(Arrays.asList(vals));
		
		public static final List<TileUpdateType> getAll() {
			return all;
		}
		
		public static final Stream<TileUpdateType> all() {
			return getAll().stream();
		}
		
	}
	
	public static enum TileSendType {
		SINGLE(),
		RECT();
		
		private static final TileSendType[] vals = TileSendType.values();
		private static final List<TileSendType> all = Collections.unmodifiableList(Arrays.asList(vals));
		
		public static final List<TileSendType> getAll() {
			return all;
		}
		
		public static final Stream<TileSendType> all() {
			return getAll().stream();
		}
		
	}
	
	private final TileUpdateType updateType;
	private final TileSendType sendType;
	private final Tile tile;
	private final Tile[][] tiles;
	
	TileMessage(Tile tile, TileUpdateType updateType) {
		sendType = TileSendType.SINGLE;
		this.updateType = updateType;
		this.tiles = null;
		this.tile = tile;
	}
	
	TileMessage(Tile[][] tiles, TileUpdateType updateType) {
		sendType = TileSendType.RECT;
		this.updateType = updateType;
		this.tiles = tiles;
		this.tile = null;
	}
	
	public TileUpdateType getUpdateType() {
		return updateType;
	}
	
	public TileSendType getSendType() {
		return sendType;
	}
	
	public Tile getTile() {
		return tile;
	}
	
	public Tile[][] getTiles() {
		return tiles;
	}
	
	public boolean isRect() {
		return TileSendType.RECT == sendType;
	}
	
	public boolean isSingle() {
		return TileSendType.SINGLE == sendType;
	}
	
	public boolean isCreate() {
		return TileUpdateType.CREATE == updateType;
	}
	
	public boolean isDestroy() {
		return TileUpdateType.DESTROY == updateType;
	}
	
}
