package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Input;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;
import com.thora.core.input.Key;

public class MenuState extends GameState {
	
	private static final Logger logger =  LogManager.getLogger(MenuState.class);
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	InputHandler handler = new InputHandler();
	InputListener inputListener = new InputListener(handler);
	
	public MenuState(String name, int id) {
		super(name, id);
		
	}
	
	public void onRender() {
		// TODO Auto-generated method stub
		//Console.log("Rendering Menu");
	
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		// Finish loading immediately and progress to next state.
		
	}
	
	@Override
	public void onCreate() {
		// 
		logger().debug("Created Menu State!");
		handler.RegisterKey(new Key("UP", Input.Keys.UP));
		
		
		
	}
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(int width, int height) {
		// TODO Auto-generated method stub
		log("Resizing");
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		log("Leaving Menu State");
	}

	@Override
	protected void Update() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void enter() {
		setFinished(true);
	}
	
}
