package com.thora.core.state;

import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Graphics;
import com.thora.core.HasLogger;
import com.thora.core.FlamesOfThora;

public abstract class GameState extends State implements HasLogger, Comparable<GameState> {
	
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
	
	public void updateAndRender() {
		float dt = Gdx.graphics.getDeltaTime();
		update(dt);
		render(dt);
	}
	
	public abstract void render(float dt);
	
	protected abstract void update(float dt);
	
	public abstract void onPause();
	
	public abstract void onResume();
	
	public abstract void initialize();
	
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
