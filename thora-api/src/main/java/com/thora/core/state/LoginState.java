package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thora.core.FlamesOfThora;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;

public class LoginState extends GameState {
	
	//Input
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);
	
	private char[] username;
	private char[] password;
 	
	//SpriteBatch for Textures
	private SpriteBatch loginScreenTextures;
	
	//Textures
	private Texture loginBackgroundTexture;
	
	//Sprites
	private Sprite loginBackground;
	
	//Constructor
	public LoginState(FlamesOfThora client, String name, int id) {
		super(client, name, id);
		// TODO Auto-generated constructor stub
	}
	
	private static final Logger logger =  LogManager.getLogger(MenuState.class);
	
	@Override
	public final Logger logger() {
		return logger;
	}

	@Override
	public void render(float dt) {
		
		loginScreenTextures.begin();
		loginBackground.draw(loginScreenTextures);
		loginScreenTextures.end();
		
		
	}

	@Override
	protected void update(float dt) {
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
	public void initialize() {
		
		logger().trace("Created Login State!");
		
		try {
			loginScreenTextures = new SpriteBatch();
			loginBackgroundTexture = new Texture(Gdx.files.internal("assets/LoginScreenBackground.png"));
			loginBackground = new Sprite(loginBackgroundTexture);
			loginBackground.setPosition((float) (Gdx.app.getGraphics().getWidth() * .5 - loginBackground.getWidth() * .5), (float) (Gdx.app.getGraphics().getHeight() * .4));
			
		} catch(Exception e) {
			
			
		}
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(int width, int height) {
		// TODO Auto-generated method stub
		loginBackground.setPosition((float) (width * .5 - loginBackground.getWidth() * .5), (float) (height * .4));
	}

	@Override
	public void enter() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		
	}

}
