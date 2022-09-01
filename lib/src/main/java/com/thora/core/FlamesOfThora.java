package com.thora.core;

import java.awt.Dimension;

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
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Pole;
import com.thora.core.world.TileGenerator;
import com.thora.core.world.World;
import com.thora.core.world.generator.PerlinTileGenerator;

public class FlamesOfThora implements ApplicationListener, Console {
	
	//TODO Compute view distance on start/resize
	public static final int DEFAULT_VIEW_RANGE = 13;
	
	public static final float DEFAULT_WORLD_SCALE = 35f;
	public static final float DEFAULT_WORLD_FREQ = 20f;
	
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
		
		TileGenerator gen = new PerlinTileGenerator((int)System.currentTimeMillis(), DEFAULT_WORLD_SCALE, DEFAULT_WORLD_FREQ);
		Dimension size = new Dimension(300,300);
		Pole origin = new Pole("Origin",0,0);
		
		//world = new KeyMapWorld(ConcurrentHashMap::new, "Earth", size, origin, gen);
		//world = new ArrayWorld("Earth", size, origin, 30, gen);
		world = new HashChunkWorld("Earth", origin, 10, 10, gen);
		
		logger().debug("World Backend: {} {}", world.getClass().getSimpleName(), world.getEstimatedArea());
		
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
