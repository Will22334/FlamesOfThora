package com.thora.client.state;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thora.client.FlamesOfThoraClient;
import com.thora.client.input.InputHandler;
import com.thora.client.input.InputListener;
import com.thora.core.HasLogger;

import io.netty.channel.ChannelFuture;

public class LoadingState<LoginTransaction> extends GameState implements HasLogger {
	
	private static final Logger logger = LogManager.getLogger(LoadingState.class);
	
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);
	private static Stage stage;
	
	private boolean renderingComplete = false;
	
	private boolean exitRequest = false;
	
	private float deltatime = Gdx.graphics.getDeltaTime();
	
	private ChannelFuture bindFuture = null;
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	private String username, password;
	
	public LoadingState(FlamesOfThoraClient client, String name, int id) {
		super(client, name, id);
		
		//Set the Input Processor to the Listener
		Gdx.input.setInputProcessor(inputListener);
		
	}
	
	public String getUsername() {
		return username;
	}

	public LoadingState<LoginTransaction> setUsername(String username) {
		this.username = username;
		return this;
	}

	public String getPassword() {
		return password;
	}

	public LoadingState<LoginTransaction> setPassword(String password) {
		this.password = password;
		return this;
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
		client().serverAddress = new InetSocketAddress("localhost",7988);
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
		this.client().network().connectAndLogin(client().serverAddress, username, password)
		.addListener(f -> {
			if(!f.isSuccess()) {
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
