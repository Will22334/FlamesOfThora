package com.thora.client;

import java.util.Objects;

import com.badlogic.gdx.ScreenAdapter;

public abstract class GameScreen extends ScreenAdapter {
	
	private final GameClient client;
	private final String name;
	
	boolean active = false;
	
	protected GameScreen(GameClient client, String name) {
		if(name == null) name = getClass().getSimpleName();
		this.name = name;
		this.client = Objects.requireNonNull(client, () -> String.format("Cannot instantiate %s with a null GameClient!", this.name));
	}
	
	public GameScreen(GameClient client) {
		this(client, null);
	}
	
	public final GameClient getClient() {
		return client;
	}
	
	public final String getName() {
		return name;
	}
	
	public final boolean isActive() {
		return active;
	}

	@Override
	public String toString() {
		return getName();
	}
	
}
