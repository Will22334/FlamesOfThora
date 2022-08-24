package com.thora.core.state;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.FlamesOfThora.Console;

public abstract class GameState extends State implements Console, Comparable<GameState> {
	
	private static final Logger logger = LogManager.getLogger("DefaultConsole");
	
	private boolean finished = false;
	
	public GameState(String name, int id) {
		super(name, id);
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	protected abstract void Update();
	
	public abstract void onRender();
	
	public abstract void onPause();
	
	public abstract void onResume();
	
	public abstract void onCreate();
	
	//Meant to have 1 ID getter?
	public int getID() {
		return getId();
	}
	
	public abstract void setName(String name);
	
	public String getName() {
		return getStateName();
	}
	
	public abstract void onResize(int width, int height);
	
	
	public abstract void enter();
	public abstract void exit();
	
	public boolean isFinished() {
		return finished;
	}
	
	public void setFinished(boolean finished) {
		this.finished = finished;
	}
	
	@Override
	public int compareTo(GameState o) {
		Objects.requireNonNull(o, "Cannot compare a GameState with null!");
		int n = getID() - o.getID();
		if(n != 0) return n;
		return getName().compareTo(o.getName());
	}
	
}
