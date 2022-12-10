package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Input;
import com.thora.core.FlamesOfThora;
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
	
	public MenuState(FlamesOfThora client, String name, int id) {
		super(client, name, id);
		
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
	public void initialize() {
		// 
		logger().trace("Created Menu State!");
		handler.RegisterKey(new Key("UP", Input.Keys.UP));
		
		
		
	}
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(int width, int height) {
		// TODO Auto-generated method stub
		logger().debug("onResize -> {}x{}", width, height);
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		logger().trace("Leaving Menu State");
	}
	
	@Override
	protected void update(float dt) {
		// TODO Auto-generated method stub
		
	}
	
	public void render(float dt) {
		// TODO Auto-generated method stub
		//Console.log("Rendering Menu");
	
	}
	
	@Override
	public void enter() {
		setFinished(true);
	}
	
}
