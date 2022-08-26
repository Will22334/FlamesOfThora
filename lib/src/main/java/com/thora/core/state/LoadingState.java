package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thora.core.FlamesOfThora.Console;
import com.thora.core.FlamesOfThora.FlamesOfThora;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;

public class LoadingState extends GameState implements Console {
	
	private static final Logger logger =  LogManager.getLogger(LoadingState.class);

	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);
	private static Stage stage;
	
	private boolean renderingComplete = false;
	
	private boolean exitRequest = false;
	
	private float deltatime = Gdx.graphics.getDeltaTime();
	
	@Override
	public final Logger logger() {
		return logger;
	}

	public LoadingState(FlamesOfThora client, String name, int id) {
		super(client, name, id);
		
		//Set the Input Processor to the Listener
		Gdx.input.setInputProcessor(inputListener);
		
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		while(this.isFinished() != true) {
			
			deltatime = Gdx.graphics.getDeltaTime();
			
			boolean renderingComplete = false;

			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(deltatime);
			stage.draw();
			
			
			//Render
			
			
			
			//Clean up
			renderingComplete = true;
			
			if(renderingComplete) {
				Update();
			}
			
//			while(renderingComplete != false) {
//				
//				Update();
//				
//				break;
//				
//			}
			
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
	public void onResize(int width, int height) {
		// TODO Auto-generated method stub
		stage.getViewport().update(width, height);
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		logger().debug("Created Loading State!!");
		stage = new Stage(new ScreenViewport());
		//System.out.println("Created Loading State!!");
		
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	public static Stage getStage() {
		return stage;
	}
	
	@Override
	public void enter() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		stage.dispose();

	}

	@Override
	protected void Update() {
		// TODO Auto-generated method stub
		while(this.isFinished() != true) {
			//Used for various things to update "separate" from the rendering.
			
			
			
			//Handle exit is set
			while(exitRequest != false) {
			setFinished(true);
			break;
			
			}
			
			break;
		}
		setFinished(true);
	}

	public boolean isRenderingComplete() {
		return renderingComplete;
	}

	public void setRenderingComplete(boolean renderingComplete) {
		this.renderingComplete = renderingComplete;
	}

	public boolean isExitRequestActivated() {
		return exitRequest;
	}

}
