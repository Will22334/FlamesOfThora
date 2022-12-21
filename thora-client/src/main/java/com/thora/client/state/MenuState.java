package com.thora.client.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.thora.client.FlamesOfThoraClient;
import com.thora.client.entities.Player;
import com.thora.core.FlamesOfThora;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;
import com.thora.core.input.Key;

public class MenuState extends GameState {
	
	private static final Logger logger =  LogManager.getLogger(MenuState.class);
	
	//Initial minimum sizes for the stage
	private static final int MINIMUMLMENUWINDOWWIDTH = 800;
	private static final int MINIMUMMENUWINDOWHEIGHT = 600;
	private int menuscreenWidth;
	private int menuscreenHeight;
	
	//A stage in which the menu will be drawn
	private static Stage menuStage;
	
	//A Table to hold UIObjects
	private static Table menuTable;
	
	//A skin for Fonts and Textures.
	private static Skin menuUISkin;
	
	
	//Some possible references to the last/next state
	//
	//
	
	//The Local Player.
	private Player player = new Player();
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	InputHandler handler = new InputHandler();
	InputListener inputListener = new InputListener(handler);
	
	public MenuState(FlamesOfThoraClient client, String name, int id) {
		super(client, name, id);
		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onResume() {
		// Finish loading immediately and progress to next state.
		
	}
	
	@Override
	public void onCreate() {
		
		//Create a stage to handle events
		menuStage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(menuStage);
		
		menuscreenWidth = Gdx.graphics.getWidth();
		menuscreenHeight = Gdx.graphics.getHeight();
		
		//Create a table to hold UI objects which defaults to an origin of 0,0 (Left Bottom Corner)
		menuTable = new Table().left().bottom();
		sizeUITable(menuscreenWidth, menuscreenHeight);
		
		//Create the Skin for the UiObjects
		menuUISkin = new Skin(Gdx.files.internal("assets/skin/uiskin.json"));
		
		//Generate a font and TextBoxStyle and add it the skin
		// Generate a 1x1 white texture and store it in the skin named "white".
		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.fill();

		menuUISkin.add("white", new Texture(pixmap));
		menuUISkin.add("default", new BitmapFont());

		// Configure a TextButtonStyle and name it "default". Skin resources are stored by type, so this doesn't overwrite the font.
		TextButtonStyle textButtonStyle = new TextButtonStyle();
		textButtonStyle.up = menuUISkin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.down = menuUISkin.newDrawable("white", Color.DARK_GRAY);
		textButtonStyle.checked = menuUISkin.newDrawable("white", Color.BLUE);
		textButtonStyle.over = menuUISkin.newDrawable("white", Color.LIGHT_GRAY);
		textButtonStyle.font = menuUISkin.getFont("default");
		menuUISkin.add("default", textButtonStyle);
		
		//======Create the Logo for FlamesOfThora========
		Image flamesofthoraLogo = new Image(new Texture("assets/LoginScreenBackground.png"));
		menuUISkin.add("default", flamesofthoraLogo);
		
		//======Create Four Buttons (Single Player, Multi-Player, Options, and Exit)=========
		
		//Single Player Button
		final TextButton sinlgeplayerButton = new TextButton("Single Player", menuUISkin);
		
		//Multi-Player Button
		final TextButton multiplayerButton = new TextButton("Multi Player", menuUISkin);
		
		//Options Button
		final TextButton optionsButton = new TextButton("Options", menuUISkin);
		
		//Exit Button
		final TextButton exitButton = new TextButton("Exit", menuUISkin);
		
		//=====Create the Labels======
	
		//Logged In: Label
		final Label loggedinLabel = new Label("Logged in as: " + player.getPlayerName(), menuUISkin);

		//Version: Label
		final Label versionLabel = new Label("Version: Are You Kidding?", menuUISkin);
		
		//=====Event Handlers=========
		
		//Single Player Button
		sinlgeplayerButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				
			}
			
				
		});
		
		//Multi-player Button
		multiplayerButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		//Options Button
		optionsButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				// TODO Auto-generated method stub
				
			}
			
			
		});
		
		//Exit button
		exitButton.addListener(new ChangeListener() {

			@Override
			public void changed(ChangeEvent event, Actor actor) {
				
				//Close the Game. :(
				menuStage.dispose();
				menuUISkin.dispose();
				Gdx.app.exit();
				
			}
			
			
		});
		
		//Add all UI objects to the table
		//menuTable.columnDefaults(1).width(150);
		menuTable.columnDefaults(0).width(200);
		menuTable.columnDefaults(1).width(200);
		menuTable.columnDefaults(2).width(200);
		
		menuTable.row().height(150);
		menuTable.add(sinlgeplayerButton).center().fill();
		menuTable.add(multiplayerButton).center().fill();
		menuTable.add(optionsButton).center().fill();
		menuTable.add(exitButton).center().fill();
		menuTable.row();
		menuTable.add(loggedinLabel);
		menuTable.add();
		menuTable.add();
		menuTable.add(versionLabel);
		
		//Add the UITable to the Stage
		menuStage.addActor(menuTable);
		
		//A debug Message!
		logger().trace("Created Menu State!");
		
	}
	
	private void sizeUITable(int windowWidth, int windowHeight) {
		
		//logger.debug("RESIZING! THE TABLE!");
		
		if(windowWidth >= MINIMUMLMENUWINDOWWIDTH || windowHeight >= MINIMUMMENUWINDOWHEIGHT) {
			
			menuTable.setFillParent(true);
			logger().debug("ResizedTableTo: -> {}x{}", windowWidth, windowHeight);
			
		} else {
			
			menuTable.setWidth(MINIMUMLMENUWINDOWWIDTH);
			menuTable.setHeight(MINIMUMMENUWINDOWHEIGHT);
			logger().debug("ResizedTableTo: -> {}x{}", MINIMUMLMENUWINDOWWIDTH, MINIMUMMENUWINDOWHEIGHT);
			
		}
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize(int width, int height) {
		// TODO Auto-generated method stub
		logger().debug("onResize -> {}x{}", width, height);
		sizeUITable(width, height);


	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub
		logger().trace("Leaving Menu State");
	}
	
	@Override
	protected void update(float dt) {
		// TODO Auto-generated method stub
		
	}
	
	public void render(float dt) {
		// TODO Auto-generated method stub
		//Console.log("Rendering Menu");
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		//Update the Actors in the Stage
		menuStage.act(dt);
		
		//Draw the UI
		menuStage.draw();
	}
	
	@Override
	public void enter() {
		
		//Entered Login State. This should only happen initially or upon logout.
		Gdx.input.setInputProcessor(menuStage);
	}
	
}
