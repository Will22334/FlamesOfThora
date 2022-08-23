package state;

import com.badlogic.gdx.Input;

import FlamesOfThora.Console;
import input.InputHandler;
import input.InputListener;
import input.Key;

public class MenuState extends GameState {

	public MenuState(String name, int id) {
		super(name, id);
		// TODO Auto-generated constructor stub
		
		}
	InputHandler handler = new InputHandler();
	InputListener inputListener = new InputListener(handler);

	@Override
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
	public void onResize() {
		// TODO Auto-generated method stub
		System.out.println("Resized!");
	}

	@Override
	public void onCreate() {
		// 
		System.out.println("Created Menu State!");
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

}
