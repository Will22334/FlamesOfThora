package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.flamesofthora.Console;

public abstract class GameState extends State implements Console {
	
	private static final Logger logger = LogManager.getLogger("DefaultConsole");
	
	public GameState(String name, int id) {
		super(name, id);
		
	}
	
	private boolean finished;
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	protected abstract void Update();
	
	public abstract void onRender();
	
	public abstract void onPause();
	
	public abstract void onResume();
	
	public abstract void onCreate();
	
	public abstract int getID();
	
	public abstract void setName(String name);
	
	public abstract String getName();

	public abstract void onResize(int width, int height);
	
	public abstract void exit();

	public boolean isFinished() {
		return finished;
	}

	public void setFinished(boolean finished) {
		this.finished = finished;
	}



}
