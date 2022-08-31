package com.thora.core.input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.thora.core.Console;

public class InputListener implements InputProcessor, Console {
	
	private static final Logger logger = LogManager.getLogger(InputListener.class);
	
	private InputHandler handler;
	
	public InputListener(InputHandler handler) {
		
		this.handler = handler;
		
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	@Override
	public boolean keyDown(int keycode) {
		
		//Key was pressed!
		
		logger().debug(() -> "KeyDown:\t" + Keys.toString(keycode) + "[" + keycode + "]");
		
		handler.handlePressedKeyEvent(keycode);
		
		//Success!
		return true;
	}
	
	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		
		handler.handleReleasedKeyEvent(keycode);
		
		return true;
	}
	
	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		
		//Log the change
		logger().debug("Mouse Event : Clicked : {} , {}", screenX, screenY);
		
		//Handle the change.
		handler.handleMouseClick(screenX, screenY);
		
		return true;
	}
	
	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		if(logger().isTraceEnabled()) {
			logger().trace("Mouse Event : Released : [{},{}] Pointer:{} ID:{}", screenX, screenY, pointer, button);
		}
		return false;
	}
	
	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		
		//Mouse Location change registered. Update the mouse location. 
		
		// Log the change.
		//Console.log("Mouse move: " + screenX + ", " + screenY);
		
		logger().trace(() -> "Mouse move: " + screenX + ", " + screenY + "]");
		
		
		
		handler.handleMousePositionChange(screenX, screenY);
		
		
		return true;
		
	}
	
	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
}