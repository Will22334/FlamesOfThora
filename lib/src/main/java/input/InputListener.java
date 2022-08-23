package input;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.InputProcessor;

import FlamesOfThora.Console;

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
		
		logger().debug(keycode);
		
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
		logger().debug("Mouse Event : Clicked : " + screenX + " , " + screenY);
		
		//Handle the change.
		handler.handleMouseClick(screenX, screenY);
		
		return true;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
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
		
		handler.handleMousePositionChange(screenX, screenY);
		
		// Log the change.
		
			//Console.log("Mouse move: " + screenX + ", " + screenY);

		return true;
				
	}

	@Override
	public boolean scrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}

}