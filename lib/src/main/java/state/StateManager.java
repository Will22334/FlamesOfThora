package state;

import FlamesOfThora.Console;

public class StateManager implements Console {

	private static StateList States = new StateList();
	
	private GameState activeState;
	private GameState lastState;
	
	private int statecounter = 0;
	
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
		
		activeState = States.getState(id);
		
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
	
}
