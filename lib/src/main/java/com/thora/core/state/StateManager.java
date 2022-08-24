package com.thora.core.state;

import com.thora.core.FlamesOfThora.Console;

public class StateManager implements Console {

	private static StateList States = new StateList();
	
	private GameState activeState;
	private GameState lastState;
	
	private int statecounter = 0;
	
	private boolean stateFinished = false;
	
	public void addStateToList(GameState State) {
		
		States.addState(State);
		statecounter = getStatecounter() + 1;
	}
	
	public void removeStateFromList(int id) {
		
		States.removeState(id);
		statecounter = getStatecounter() - 1;
	}
	
	public void setActiveState(int id) {
		
		lastState = activeState;
		
		if(lastState != null) lastState.onPause();
		activeState = States.getState(id);
		if(activeState != null) activeState.onResume();
		
		
		log("Setting " + activeState.getStateName() + " as the active state");
		
	}
	
	public GameState getActiveState() {
		
		return activeState;
		
	}
	
	public void ReturnToLastState() {
		
		activeState = lastState;
	}
	
	//Runs the Create Method for all states in the list
	public void onCreate() {
		
		int i = 0;
		
		while(i < getStates().getSize()) {
			
			getStates().getGameStates().get(i).onCreate();
			
			i++;
		}
		
	}

	public static StateList getStates() {
		return States;
	}

	public int getStatecounter() {
		return statecounter;
	}

	public void checkForExit() {
		// TODO Auto-generated method stub
		while(getActiveState().isFinished() != false) {
			
			setStateFinished(true);
			break;
			
		}
	}

	public boolean isStateFinished() {
		return stateFinished;
	}

	public void setStateFinished(boolean stateFinished) {
		this.stateFinished = stateFinished;
	}
	
}
