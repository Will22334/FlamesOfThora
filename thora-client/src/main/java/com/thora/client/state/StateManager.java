package com.thora.client.state;

import java.util.Map;

import org.apache.logging.log4j.Logger;

import com.thora.client.FlamesOfThoraClient;
import com.thora.core.HasLogger;

public class StateManager implements HasLogger {

	private static StateList States = new StateList();
	
	private GameState currentState;
	private GameState prevState;
	
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
		
		//Keep local references to current and new state before altering member fields
		GameState newState = States.getState(id);
		GameState oldState = currentState;
		
		logger().info("Changing State: [{}] -> [{}]", oldState, newState);
		
		if(oldState != null) {
			oldState.exit();
		}
		
		prevState = oldState;
		currentState = newState;
		
		setStateFinished(false);
		
		if(currentState != null) {
			currentState.enter();
		}
		
		
//		prevState = currentState;
//		
//		if(prevState != null) prevState.exit();
//		currentState = States.getState(id);
//		setStateFinished(false);
//		if(currentState != null) currentState.enter();
//		
//		
//		//log("Setting " + activeState.getStateName() + " as the active state");
//		logger().info("GameState: [{}] -> [{}]", prevState, currentState);
		
	}
	
	public void setNextState() {
		setActiveState(getActiveState().getID() + 1);
	}
	
	public GameState getActiveState() {
		
		return currentState;
		
	}
	
	public void ReturnToLastState() {
		
		currentState = prevState;
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

	@Override
	public Logger logger() {
		return FlamesOfThoraClient.logger;
	}
	
}
