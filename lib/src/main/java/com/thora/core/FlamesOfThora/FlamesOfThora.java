package com.thora.core.FlamesOfThora;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationListener;
import com.thora.core.state.GameState;
import com.thora.core.state.LoadingState;
import com.thora.core.state.MenuState;
import com.thora.core.state.PlayingState;
import com.thora.core.state.StateManager;

public class FlamesOfThora implements ApplicationListener, Console {
	
	private final static int LOADINGSTATEID = 0;
	private final static int MENUSTATEID = 1;
	private final static int PLAYINGSTATEID = 2;
	
	public static final Logger logger = LogManager.getLogger("Client");
	
	//Manages the various states and provides switching between them
	public StateManager States = new StateManager();
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	//Initiate the States
	public void initializeStates() {
		States.addStateToList(new MenuState("Menu State", MENUSTATEID));
		States.addStateToList(new PlayingState("Playing State", PLAYINGSTATEID));
		States.addStateToList(new LoadingState("Loading State", LOADINGSTATEID));
		
	}
	
	//Create the 
	@Override
	public void create() {
		
		//Add the states to the StateManager for indexing and management.
		initializeStates();
		
		//Runs the create command for all states.
		States.onCreate();
		
		States.setActiveState(LOADINGSTATEID);
		
	}
	
	//Called whenever the Application is resized.
	@Override
	public void resize(int width, int height) {
		// Resize the Application
		
		States.getActiveState().onResize(width, height);
		
	}
	
	@Override
	public void render() {
		// Render the game based on the current state
		
		States.getActiveState().onRender();
		
//		while(States.isStateFinished() != false) {
//			
//			log("Detected change in state. :  Exited : in " + States.getActiveState().getName());
//			States.setActiveState(States.getActiveState().getID() + 1);
//			
//		}
		if(States.isStateFinished()) {
			logger().info("Detected change in state. :  Exited : in {}", States.getActiveState());
			States.setNextState();
		}
		States.checkForExit();
	}
	
	@Override
	public void pause() {
		
		States.getActiveState().onPause();
		
	}
	
	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
	
	
	public static int getLoadingstate() {
		return LOADINGSTATEID;
	}
	
	public static int getMenustateid() {
		return MENUSTATEID;
	}
	
	public static int getPlayingstateid() {
		return PLAYINGSTATEID;
	}
	
	
}
