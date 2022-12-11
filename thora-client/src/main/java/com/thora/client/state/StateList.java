package com.thora.client.state;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class StateList {	
	
	private Map<Integer, GameState> gameStates = new HashMap<Integer, GameState>();
	
	public void addState(GameState state) {
		Objects.requireNonNull(state, "Cannot add a null GameState to StateList!");
		gameStates.put(state.getID(), state);
	}
	
	/**
	 *  Retrieve and return the current game sate associated with the given ID.
	 * @param ID the id of the state to retrieve
	 * @return The current game state for that ID,
	 * NULL if there is no game state with that ID.
	 */
	public GameState getState(int ID) {
		return gameStates.get(ID);
	}
	
	/**
	 * Remove the state associated with the given ID.
	 * @param ID state ID
	 * @return The previous game state that was removed.
	 * NULL if there was no game state with the ID.
	 */
	public GameState removeState(int ID) {
		return gameStates.remove(ID);
	}
	
	/**
	 * Removes the state that has the given states ID.
	 * @param state state with ID to remove
	 * @return Previous GameState associated with the ID.
	 * NULL if there was no state associated.
	 */
	public GameState removeState(GameState state) {
		Objects.requireNonNull(state, "Cannot remove a null GameState from StateList!");
		return removeState(state.getID());
	}
	
	public Map<Integer, GameState> getGameStates() {
		return gameStates;
	}
	
	public int getSize() {
		return gameStates.size();
	}
	
}
