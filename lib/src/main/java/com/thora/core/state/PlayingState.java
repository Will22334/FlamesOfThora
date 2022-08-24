package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;

public class PlayingState extends GameState {
	
	private static final Logger logger =  LogManager.getLogger(PlayingState.class);
	private static final InputHandler inputHandler = new InputHandler();
	private static final InputListener inputListener = new InputListener(inputHandler);
	
	private SpriteBatch batch;
	private BitmapFont font;
	
	public PlayingState(String name, int id) {
		super(name, id);
	}
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		logger().info("Created Playing State!!");
	}
	
	@Override
	public void onRender() {
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		batch.begin();
		font.setColor(Color.RED);
		font.draw(batch, "FPS: " + Gdx.graphics.getFramesPerSecond(), 0, Gdx.graphics.getHeight() - 2);
		batch.end();
	}
	
	@Override
	public void onPause() {
		batch.dispose();
		font.dispose();
	}
	
	@Override
	public void onResume() {
		batch = new SpriteBatch();
		font = new BitmapFont();
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
	
	public static InputListener getInputlistener() {
		return inputListener;
	}
	
	@Override
	public void onResize(int width, int height) {
		batch.dispose();
		font.dispose();
		batch = new SpriteBatch();
		font = new BitmapFont();
	}
	
	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void Update() {
		// TODO Auto-generated method stub
		
	}
	
}
