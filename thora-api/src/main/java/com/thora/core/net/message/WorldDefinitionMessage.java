package com.thora.core.net.message;

import com.thora.core.world.World;

public class WorldDefinitionMessage extends AbstractThoraMessage {
	
	private final World world;
	
	public WorldDefinitionMessage(World world) {
		this.world = world;
	}
	
	public final World world() {
		return world;
	}
	
}
