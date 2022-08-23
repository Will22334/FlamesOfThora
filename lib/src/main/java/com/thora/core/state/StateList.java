package com.thora.core.state;

import java.util.HashMap;

public class StateList {	
	
	private HashMap<Integer, GameState> GameStates = new HashMap<Integer, GameState>();

	public void addState(GameState state) {
		
		GameStates.put(state.getID(), state);
		
	}
	
	//Check if there is a GameState with the ID passed in, and if so, return said GameState,
	// or null if none exists.
	
	public GameState getState(int ID) {
		
		while(GameStates.containsKey(ID)) {
			
			return GameStates.get(ID);
			
		}
		return null;
		
	}
	
	//Remove a State from the List
	
	public void removeState(int ID) {
		
		while(GameStates.containsKey(ID)) {
			
			GameStates.remove(ID);
		}
	}

	public HashMap<Integer, GameState> getGameStates() {
		return GameStates;
	}
	
	public int getSize() {
		
		return GameStates.size();
	}

}
