package com.thora.core.net.message;

import com.thora.core.world.Tile;

public class TileMessage extends AbstractThoraMessage {
	
	public static enum TileUpdateType {
		CREATE(),
		UPDATE(),
		DESTROY();
		
		private static final TileUpdateType[] vals = TileUpdateType.values();
		
	}
	
	public static enum TileSendType {
		RECT(),
		SET();
		
		private static final TileSendType[] vals = TileSendType.values();
		
	}
	
	private Tile[][] tileRect;
	
	
}
