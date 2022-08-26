package com.thora.core.FlamesOfThora;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.thora.core.state.GameState;
import com.thora.core.state.LoadingState;
import com.thora.core.state.MenuState;
import com.thora.core.state.PlayingState;
import com.thora.core.state.StateManager;
import com.thora.core.world.ArrayWorld;
import com.thora.core.world.TileGenerator.RandomTileGenerator;
import com.thora.core.world.World;

public class FlamesOfThora implements ApplicationListener, Console {
	
	private final static int LOADINGSTATEID = 0;
	private final static int MENUSTATEID = 1;
	private final static int PLAYINGSTATEID = 2;
	
	public static final Logger logger = LogManager.getLogger("Client");
	
	private PooledEngine engine = new PooledEngine();
	
	//Manages the various states and provides switching between them
	public StateManager States = new StateManager();
	
	private World world;
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	public PooledEngine engine() {
		return engine;
	}
	
	public World world() {
		return world;
	}
	
	//Initiate the States
	public void initializeStates() {
		States.addStateToList(new MenuState(this, "Menu State", MENUSTATEID));
		States.addStateToList(new PlayingState(this, "Playing State", PLAYINGSTATEID));
		States.addStateToList(new LoadingState(this, "Loading State", LOADINGSTATEID));
		
	}
	
	protected GameState activeState() {
		return States.getActiveState();
	}
	
	//Create the 
	@Override
	public void create() {
		
		//Add the states to the StateManager for indexing and management.
		initializeStates();
		
		//Runs the create command for all states.
		States.onCreate();
		
		//world = new ArrayWorld("Earth", 50, 30, new PerlinTileGenerator((int)System.currentTimeMillis(), 140f));
		world = new ArrayWorld("Earth", 50, 30, new RandomTileGenerator(new Random()));
		
		States.setActiveState(LOADINGSTATEID);
		
	}
	
	//Called whenever the Application is resized.
	@Override
	public void resize(int width, int height) {
		// Resize the Application
		
		activeState().onResize(width, height);
		
	}
	
	@Override
	public void render() {
		// Render the game based on the current state
		
		activeState().onRender();
		
//		while(States.isStateFinished() != false) {
//			
//			log("Detected change in state. :  Exited : in " + States.getActiveState().getName());
//			States.setActiveState(States.getActiveState().getID() + 1);
//			
//		}
		if(States.isStateFinished()) {
			logger().trace("Detected change in state. :  Exited : in {}", activeState());
			States.setNextState();
		}
		States.checkForExit();
	}
	
	@Override
	public void pause() {
		
		activeState().onPause();
		
	}
	
	@Override
	public void resume() {
		
		activeState().onResume();
		
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
