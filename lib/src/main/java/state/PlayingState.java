package state;

import input.InputHandler;
import input.InputListener;

public class PlayingState extends GameState {
	
	public PlayingState(String name, int id) {
		super(name, id);
		
		
	}
	
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("Created Playing State!!");
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
	public void onResize() {
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

}
