package com.thora.client.entities;

public class Player {

	private String playerName;
	
	private PlayerInventory playerInventory;
	
	
	//A basic empty player.
	public Player() {
		
		this.playerName = "";
		this.playerInventory = new PlayerInventory();
	
	}
	
	//A new player who already has a name but needs an inventory.
	public Player(String playerName) {
		
		this.playerName = playerName;
		this.playerInventory = new PlayerInventory();
		
	}
	
	//A new player who already has a name but needs an inventory.
	public Player(String playerName, PlayerInventory playerInventory) {
		
		this.playerName = playerName;
		this.playerInventory = playerInventory;
		
	}

	public String getPlayerName() {
		return playerName;
	}

	public PlayerInventory getPlayerInventory() {
		return playerInventory;
	}
	
	
}
