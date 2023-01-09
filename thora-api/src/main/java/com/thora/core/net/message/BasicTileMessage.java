package com.thora.core.net.message;

import com.thora.core.world.Location;
import com.thora.core.world.Tile;
import com.thora.core.world.TileData;

/**
 * A Server -> Client only packet that informs client of world tiles.
 * @author Dave
 *
 */
public class BasicTileMessage extends AbstractThoraMessage {
	
	public static BasicTileMessage createRegion(Location bottomLeft, TileData[][] tiles) {
		return new  BasicTileMessage(bottomLeft, tiles);
	}
	
	public static BasicTileMessage createSingle(Location bottomLeft, TileData data) {
		return new  BasicTileMessage(bottomLeft, data);
	}
	
	public static BasicTileMessage createSingle(Tile tile) {
		return new  BasicTileMessage(tile.getLocation(), tile.getTileData());
	}
	
	//Rect of Tiles
	public TileData[][] tiles;
	public Location bottomLeft;
	
	public TileData data;
	
	public boolean isGroup() {
		return tiles != null;
	}
	
	public boolean isSingle() {
		return data != null;
	}
	
	public int size() {
		if(isGroup()) {
			return tiles.length * tiles[0].length;
		} else {
			return 1;
		}
	}
	
	protected BasicTileMessage(Location bottomLeft, TileData[][] tiles) {
		this.bottomLeft = bottomLeft;
		this.tiles = tiles;
	}
	
	protected BasicTileMessage(Location bottomLeft, TileData data) {
		this.bottomLeft = bottomLeft;
		this.data = data;
	}
	
}
