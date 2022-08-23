package state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;

import FlamesOfThora.Console;
import input.InputHandler;
import input.InputListener;

public class LoadingState extends GameState implements Console {
	
	private static final Logger logger =  LogManager.getLogger(LoadingState.class);
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	public boolean isFinished() {
		return finished;
	}

	private void setFinished(boolean finished) {
		this.finished = finished;
	}

	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);

	public LoadingState(String name, int id) {
		super(name, id);
		
		//Set the Input Processor to the Listener
		Gdx.input.setInputProcessor(inputListener);
		
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		while(this.isFinished() != true) {
			
			boolean condition = false;
			
			condition = true;
			
			while(condition != false) {
				
				this.setFinished(true);
				break;
			}
			
			break;
		}
		
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
	public void onCreate() {
		// TODO Auto-generated method stub
		logger().info("Created Loading State!!");
		//System.out.println("Created Loading State!!");
		
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
		
	}

}
