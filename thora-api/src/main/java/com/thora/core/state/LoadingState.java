package com.thora.core.state;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thora.core.Console;
import com.thora.core.FlamesOfThora;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;

import io.netty.channel.ChannelFuture;

public class LoadingState extends GameState implements Console {
	
	private static final Logger logger = LogManager.getLogger(LoadingState.class);
	
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);
	private static Stage stage;
	
	private boolean renderingComplete = false;
	
	private boolean exitRequest = false;
	
	private float deltatime = Gdx.graphics.getDeltaTime();
	
	private InetSocketAddress serverAddress = null;
	private ChannelFuture bindFuture = null;
	
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
	public void render(float dt) {
		// TODO Auto-generated method stub
		while(this.isFinished() != true) {
			
			deltatime = dt;
			
			boolean renderingComplete = false;
			
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			stage.act(deltatime);
			stage.draw();
			
			
			//Render
			
			
			
			//Clean up
			renderingComplete = true;
			
			if(renderingComplete) {
				update(deltatime);
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
		stage.getViewport().update(width, height);
	}
	
	@Override
	public void onCreate() {
		serverAddress = new InetSocketAddress("localhost",7988);
		stage = new Stage(new ScreenViewport());
		logger().trace("Created Loading State!");
	}
	
	@Override
	public void setName(String name) {
		
	}
	
	public static Stage getStage() {
		return stage;
	}
	
	@Override
	public void enter() {
		this.client().network().connectAndLogin(serverAddress, "user", "pwd")
		.addListener(f -> {
			if(f.isSuccess()) {
				
				//this.setFinished(true);
			} else {
				logger().atWarn().withThrowable(f.cause()).log("Could not login due to exception!");
				Gdx.app.exit();
			}
		});
	}
	
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		stage.dispose();
		
	}
	
	@Override
	protected void update(float dt) {
		// TODO Auto-generated method stub
//		if(!isFinished()) {
//			//Used for various things to update "separate" from the rendering.
//			//Handle exit is set
//			if(exitRequest) {
//				setFinished(true);
//			}
//		}
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
