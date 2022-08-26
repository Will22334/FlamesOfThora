package com.thora.core.world;

import com.badlogic.ashley.core.Component;

public class TileTypeComponent implements Component {
	
	private TileType tiletype;

	public final TileType getTileType() {
		return tiletype;
	}

	public final TileTypeComponent setTileType(TileType tiletype) {
		this.tiletype = tiletype;
		return this;
	}
	
	public final TileTypeComponent set(TileType type) {
		return setTileType(type);
	}
	
}
