package com.thora.core.state;

import java.util.Map;

import com.thora.core.Console;

public class StateManager implements Console {

	private static StateList States = new StateList();
	
	private GameState activeState;
	private GameState lastState;
	
	private int statecounter = 0;
	
	private boolean stateFinished = false;
	
	public void addStateToList(GameState State) {
		
		States.addState(State);
		++statecounter;
	}
	
	public void removeStateFromList(int id) {
		
		States.removeState(id);
		--statecounter;
	}
	
	public void setActiveState(int id) {
		
		lastState = activeState;
		
		if(lastState != null) lastState.exit();
		activeState = States.getState(id);
		setStateFinished(false);
		if(activeState != null) activeState.enter();
		
		
		//log("Setting " + activeState.getStateName() + " as the active state");
		logger().info("Gamstate: [{}] -> [{}]", lastState, activeState);
		
	}
	
	public void setNextState() {
		setActiveState(getActiveState().getID() + 1);
	}
	
	public GameState getActiveState() {
		
		return activeState;
		
	}
	
	public void ReturnToLastState() {
		
		activeState = lastState;
	}
	
	//Runs the Create Method for all states in the list
	public void onCreate() {
		
//		int i = 0;
//		
//		while(i < getStates().getSize()) {
//			
//			getStates().getGameStates().get(i).onCreate();
//			
//			i++;
//		}
		
		//Call onCreate() for all game states in ID order
		Map<Integer,GameState> states = getStates().getGameStates();
		states.values().stream()
		.sorted()
		.forEach(s -> s.onCreate());
		
	}

	public static StateList getStates() {
		return States;
	}

	public int getStatecounter() {
		return statecounter;
	}

	public void checkForExit() {
		if(getActiveState().isFinished()) {
			setStateFinished(true);
		}
	}

	public boolean isStateFinished() {
		return stateFinished;
	}

	public void setStateFinished(boolean stateFinished) {
		this.stateFinished = stateFinished;
	}
	
	public void finishState() {
		setStateFinished(true);
	}
	
}
