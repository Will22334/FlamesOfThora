package com.thora.core.state;

import java.net.InetSocketAddress;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thora.core.HasLogger;
import com.thora.core.FlamesOfThora;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;
import com.thora.core.net.LoginTransaction;

import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;

@Deprecated
public class OldLoadingState extends GameState implements HasLogger {
	
	private static final Logger logger = LogManager.getLogger(OldLoadingState.class);
	
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);
	private static Stage stage;
	
	private boolean renderingComplete = false;
	
	private boolean exitRequest = false;
	
	private float deltatime = Gdx.graphics.getDeltaTime();
	
	private InetSocketAddress serverAddress = null;
	private ChannelFuture bindFuture = null;
	
	private Future<LoginTransaction> loginFuture = null;
	
	private SpriteBatch loadingScreenGraphics;
	private Sprite loadingBar;
	
	private Texture loadingBarTexture;
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	public OldLoadingState(FlamesOfThora client, String name, int id) {
		super(client, name, id);
		
		//Set the Input Processor to the Listener
		Gdx.input.setInputProcessor(inputListener);
		loadingScreenGraphics = new SpriteBatch();
		
	}
	
	@Override
	public void render(float dt) {
		// TODO Auto-generated method stub
		while(this.isFinished() != true) {
			
			deltatime = dt;
			
			boolean renderingComplete = false;
			
			//Clears the Screen
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			
			stage.act(deltatime);
			stage.draw();
			
			//Render
			loadingScreenGraphics.begin();
			loadingBar.draw(loadingScreenGraphics);
			loadingScreenGraphics.end();
			
			//Clean up. Rendering Done.
			renderingComplete = true;
			
			//Update the time
			if(renderingComplete) {
				update(deltatime);
			}
			
			while(renderingComplete != false) {
				
				Update();
				
				break;
				
			}
			
			break;
		}
		
	}
	
	private void Update() {
		
		//Updates the time spent in the state
		
		deltatime += Gdx.graphics.getDeltaTime() - deltatime;
		
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
	public void initialize() {
		//Define the connection to connect to,
		serverAddress = new InetSocketAddress("127.0.0.1",5000);
		
		//Define a stage in which...
		stage = new Stage(new ScreenViewport());
		
		//Create the Loading bar.
		loadingBarTexture = new Texture(Gdx.files.internal("assets/loadingBar.png"));
		loadingBar = new Sprite(loadingBarTexture);
		loadingBar.setPosition((float) (Gdx.app.getGraphics().getWidth() * .5 - loadingBarTexture.getWidth() * .5), (float) (Gdx.app.getGraphics().getHeight() * .3));
		
		
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
		logger().warn("Using deprecated Core/Client State: {}", getClass().getName());
		bindFuture = client().network().connect(serverAddress);
		loginFuture = client().network().attachLoginListener(bindFuture, "user", "pwd")
				.addListener((Future<LoginTransaction> f) -> {
					if(f.isSuccess()) {
						final LoginTransaction loginTransaction = f.get();
						logger().debug("Login Request for user {} === {}", loginTransaction.request.username, loginTransaction.response);
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
