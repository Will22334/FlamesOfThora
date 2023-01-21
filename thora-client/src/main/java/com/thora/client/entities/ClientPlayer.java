package com.thora.client.entities;

import com.thora.core.world.WorldEntity;

public class ClientPlayer {
	
	private String playerName;
	
	private WorldEntity entity;
	
	private PlayerInventory playerInventory;
	
	
	//A basic empty player.
	public ClientPlayer() {
		this("");
	}
	
	//A new player who already has a name but needs an inventory.
	public ClientPlayer(String playerName) {
		this(playerName, null, new PlayerInventory());
	}
	
	//A new player who already has a name but needs an inventory.
	public ClientPlayer(String playerName, PlayerInventory playerInventory) {
		this(playerName, null, playerInventory);
	}
	
	public ClientPlayer(final String playerName, final WorldEntity entity, final PlayerInventory playerInventory) {
		this.playerName = playerName;
		this.entity = entity;
		this.playerInventory = playerInventory;
	}
	
	public String getPlayerName() {
		return playerName;
	}
	
	public WorldEntity getEntity() {
		return entity;
	}
	
	public void setEntity(WorldEntity entity) {
		this.entity = entity;
	}
	
	public PlayerInventory getPlayerInventory() {
		return playerInventory;
	}
	
	
}
