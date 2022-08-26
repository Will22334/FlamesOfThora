package com.thora.core.state;

import com.badlogic.gdx.ScreenAdapter;
import com.thora.core.FlamesOfThora.Client;

public class GameScreen extends ScreenAdapter {
	
	private final Client client;
	private final String name;
	
	public GameScreen(Client client, String name) {
		this.client = client;
		this.name = name;
	}
	
	public final Client getClient() {
		return client;
	}
	
	public final String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return getName();
	}
	
}
