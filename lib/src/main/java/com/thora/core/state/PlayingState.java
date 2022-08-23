package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;

public class PlayingState extends GameState {
	
	private static final Logger logger =  LogManager.getLogger(LoadingState.class);
	private static final InputHandler inputHandler = new InputHandler();
	private static final InputListener inputListener = new InputListener(inputHandler);
	
	public final Logger logger() {
		return logger;
	}
	
	public PlayingState(String name, int id) {
		super(name, id);
		
		
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		logger().info("Created Playing State!!");
	}
	
	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return this.getId();
	}
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getName() {
		return this.getStateName();
		// TODO Auto-generated method stub
		
	}

	public static InputListener getInputlistener() {
		return inputListener;
	}

	@Override
	public void onResize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Update() {
		// TODO Auto-generated method stub
		
	}
	
}
