package state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Input;

import input.InputHandler;
import input.InputListener;
import input.Key;

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
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onCreate() {
		// 
		log("Created Menu State!");
		handler.RegisterKey(new Key("UP", Input.Keys.UP));
		
		
		
	}
	
	@Override
	public int getID() {
		//Returns the ID of the State;
		return this.getId();
	}
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getStateName();
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
	
}
