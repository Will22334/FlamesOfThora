package com.thora.core;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

public class GameClient implements ApplicationListener, Console {
	
	public static final Logger logger = LogManager.getLogger(GameClient.class);
	
	private Map<Class<GameScreen>,GameScreen> screens = new HashMap<>();
	private GameScreen currentScreen;
	private GameScreen prevScreen;
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	public <S extends GameScreen> S getScreen(Class<S> c) {
		return (S) screens.get(c);
	}
	
	public <S extends GameScreen> void addScreen(Class<GameScreen> c, S screen) {
		screens.put(c, screen);
	}
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void dispose () {
		if (currentScreen != null) currentScreen.hide();
	}
	
	@Override
	public void pause () {
		if (currentScreen != null) currentScreen.pause();
	}
	
	@Override
	public void resume () {
		if (currentScreen != null) currentScreen.resume();
	}
	
	@Override
	public void render () {
		if (currentScreen != null) currentScreen.render(Gdx.graphics.getDeltaTime());
	}
	
	@Override
	public void resize (int width, int height) {
		if (currentScreen != null) currentScreen.resize(width, height);
	}
	
	/** Sets the current screen. {@link Screen#hide()} is called on any old screen, and {@link Screen#show()} is called on the new
	 * screen, if any.
	 * @param newScreen may be {@code null}
	 * @return the previously active {@link GameScreen} which may be {@code null}
	 */
	public GameScreen setScreen (GameScreen newScreen) {
		if (currentScreen != null) currentScreen.hide();
		
		prevScreen = currentScreen;
		currentScreen = newScreen;
		
		if (currentScreen != null) {
			currentScreen.show();
			currentScreen.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		}
		return prevScreen;
	}
	
	/**
	 * @return the currently active {@link GameScreen}.
	 */
	public GameScreen getScreen () {
		return currentScreen;
	}
	
}
