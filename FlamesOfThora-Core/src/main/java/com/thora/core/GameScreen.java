package com.thora.core;

import com.badlogic.gdx.ScreenAdapter;

public abstract class GameScreen extends ScreenAdapter {
	
	private final GameClient client;
	private final String name;
	
	public GameScreen(GameClient client, String name) {
		this.client = client;
		this.name = name;
	}
	
	public final GameClient getClient() {
		return client;
	}
	
	public final String getName() {
		return name;
	}
	
	
	
}
