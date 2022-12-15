package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thora.core.FlamesOfThora;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;
import com.thora.core.ui.UIPosition;
import com.thora.core.ui.UITextBox;

public class LoginState extends GameState {
	
	//Some Constants about the Login Window
	private static final int MINIMUMLOGINWINDOWWIDTH = 300;
	private static final int MINIMUMLOGINWINDOWHIEGHT = 250;
	
	//Input
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);
	
	//Stage for UI Objects
	private Stage uiStage;
	
	private Skin skin;
	
	//Table for UI Objects
	Table loginscreenuiTable;
	
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
	public void render(float dt) {
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Render the UI
		uiStage.act(dt);
		uiStage.draw();
		
	}
	@Override
	public void initialize() {
		
		try {
			
			skin = new Skin(Gdx.files.internal("assets/skin/uiskin.json"));
			
			loginscreenuiTable = new Table();
			
			// Create the UI Stage
			uiStage = new Stage(new ScreenViewport());
			Gdx.input.setInputProcessor(uiStage);
			
			//Create the background
			Image loginscreenBackground = new Image(new Texture("assets/LoginScreenBackground.png"));
			skin.add("background", loginscreenBackground);
		
			// Generate a 1x1 white texture and store it in the skin named "white".
			Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
			pixmap.setColor(Color.WHITE);
			pixmap.fill();
			
			skin.add("white", new Texture(pixmap));
			skin.add("default", new BitmapFont());

			// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
			TextButtonStyle textButtonStyle = new TextButtonStyle();
			textButtonStyle.up = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.down = skin.newDrawable("white", Color.DARK_GRAY);
			textButtonStyle.checked = skin.newDrawable("white", Color.BLUE);
			textButtonStyle.over = skin.newDrawable("white", Color.LIGHT_GRAY);
			textButtonStyle.font = skin.getFont("default");
			skin.add("default", textButtonStyle);
			
			//Create the TextBoxes for the username and password
			skin.get(LabelStyle.class).font.getData().markupEnabled = true;
			
			final Label usernameLabel = new Label("Username: ", skin);
			
			final Label passwordLabel = new Label("Password: ", skin);
			
			//User Name Text Field
			final UITextBox usernameField = new UITextBox("Username: ", new UIPosition((int) (Gdx.graphics.getWidth() * 0.5), (int) (Gdx.graphics.getHeight() * 0.3)));
			usernameField.setColor(Color.GRAY);
			
			//Password text Field
			final UITextBox passwordField = new UITextBox("Password: ", new UIPosition((int) (Gdx.graphics.getWidth() * 0.5), (int) (Gdx.graphics.getHeight() * 0.4)));
			
			//Login Button
			final TextButton loginbutton = new TextButton("Login", skin);
			
			//Exit button
			final TextButton exitbutton = new TextButton("Exit", skin);
			
			loginscreenuiTable.add(usernameLabel);
			loginscreenuiTable.add(usernameField);
			loginscreenuiTable.row();
			loginscreenuiTable.add(passwordLabel);
			loginscreenuiTable.add(passwordField);
			loginscreenuiTable.row();
			loginscreenuiTable.add(loginbutton);
			loginscreenuiTable.add();
			loginscreenuiTable.add(exitbutton);
			
			loginscreenuiTable.setPosition((float) (Gdx.graphics.getWidth() * 0.5 - MINIMUMLOGINWINDOWWIDTH * 0.5), (float) (Gdx.graphics.getHeight() * 0.5 - MINIMUMLOGINWINDOWHIEGHT * 0.5));
			loginscreenuiTable.setWidth(MINIMUMLOGINWINDOWWIDTH);
			loginscreenuiTable.setHeight(MINIMUMLOGINWINDOWHIEGHT);
			
			uiStage.addActor(loginscreenuiTable);
			//Add an event to the login button
			
			loginbutton.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					System.out.println("Logging in! : " + loginbutton.isChecked());
					loginbutton.setText("...");
					exit();
				}
			});
			
			//Add an event to the exit button
			exitbutton.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					System.out.println("Exiting! : " + exitbutton.isChecked());
					loginbutton.setText("...");
					Gdx.app.exit();
				}
			});
			
			//Success
			logger().trace("Created Login State!");
			
		} catch(Exception e) {
			
			//Failure
			logger().trace("Failed to properly create Login State!");
			
		}
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(int width, int height) {
		
		//Update the UI Viewport
		uiStage.getViewport().update(width, height, true);
		loginscreenuiTable.setPosition((float) (width * 0.5 - MINIMUMLOGINWINDOWWIDTH * 0.5), (float) (height * 0.5 - MINIMUMLOGINWINDOWHIEGHT * 0.5));
		loginscreenuiTable.setWidth(MINIMUMLOGINWINDOWWIDTH);
		loginscreenuiTable.setHeight(MINIMUMLOGINWINDOWHIEGHT);
	}

	@Override
	public void enter() {
		
		//Entered Login State. This should only happen initially or upon logout.
		Gdx.input.setInputProcessor(uiStage);
		
	}

	@Override
	public void exit() {
		
		//Delete the UI Stage
		
		skin.dispose();
		this.setFinished(true);
	}

}
