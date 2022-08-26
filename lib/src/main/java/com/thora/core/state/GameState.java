package com.thora.core.state;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.thora.core.FlamesOfThora.Console;
import com.thora.core.FlamesOfThora.FlamesOfThora;

public abstract class GameState extends State implements Console, Comparable<GameState> {
	
	private static final Logger logger = LogManager.getLogger("DefaultConsole");
	
	private final FlamesOfThora client;
	private boolean finished = false;
	
	public GameState(FlamesOfThora client, String name, int id) {
		super(name, id);
		this.client = client;
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	protected final FlamesOfThora client() {
		return client;
	}
	
	protected final Graphics g() {
		return Gdx.graphics;
	}
	
	protected final PooledEngine engine() {
		return client().engine();
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
