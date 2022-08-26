package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.thora.core.FlamesOfThora.FlamesOfThora;
import com.thora.core.entity.EntityType;
import com.thora.core.entity.PlayerComponent;
import com.thora.core.entity.TypeComponent;
import com.thora.core.graphics.RenderingSystem;
import com.thora.core.graphics.TextureComponent;
import com.thora.core.graphics.TransformComponent;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;
import com.thora.core.input.Key;
import com.thora.core.world.LocationComponent;

public class PlayingState extends GameState {
	
	private static final Logger logger =  LogManager.getLogger(PlayingState.class);
	private static final InputHandler inputHandler = new InputHandler();
	private static final InputListener inputListener = new InputListener(inputHandler);
	
	private static final Key KEY_ESCAPE = new Key(Keys.ESCAPE);
	
	private RenderingSystem renderingSystem;
	
	public static final Matrix4 NATIVE_MATRIX = new Matrix4();
	
	private Camera worldCamera;
	private SpriteBatch worldBatch;
	private SpriteBatch hudBatch;
	
	private BitmapFont font;
	Texture playerImg;
	TextureRegion playerImgRegion;
	
	private Signal<Void> resizeSignal = new Signal<>();
	
	public PlayingState(FlamesOfThora client, String name, int id) {
		super(client, name, id);
	}
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		logger().debug("Created Playing State!!");
	}
	
	Matrix4 uiMatrix = new Matrix4();
	
	@Override
	public void onRender() {
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		engine().update(Gdx.graphics.getDeltaTime());
		
		
		//worldCamera.update();
		uiMatrix.set(worldCamera.combined);
		uiMatrix.setToOrtho2D(0, 0, g().getWidth(), g().getHeight());
		
		//batch.setTransformMatrix(hudCamera.combined);
		hudBatch.setProjectionMatrix(uiMatrix);
		hudBatch.begin();
		
		font.setColor(Color.RED);
		String msg = String.format("FPS: %s\t(%s,%s)", g().getFramesPerSecond(), Gdx.input.getX(), Gdx.input.getY());
		font.draw(hudBatch, msg, 0, g().getHeight());
		
		
		
		hudBatch.end();
		
		Update();
	}
	
	@Override
	public void onPause() {
		
	}
	
	@Override
	public void onResume() {
		
	}
	
	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}
	
	public static InputListener getInputlistener() {
		return inputListener;
	}
	
	@Override
	public void onResize(int width, int height) {
		worldCamera.update();
//		hudBatch.dispose();
//		worldBatch.dispose();
//		font.dispose();
//		hudBatch = new SpriteBatch();
//		worldBatch = new SpriteBatch();
//		font = new BitmapFont();
	}
	
	@Override
	public void enter() {
		hudBatch = new SpriteBatch();
		worldBatch = new SpriteBatch();
		font = new BitmapFont();
		playerImg = new Texture("assets/player.png");
		playerImgRegion = new TextureRegion(playerImg);
		Gdx.input.setInputProcessor(inputListener);
		inputHandler.RegisterKey(KEY_ESCAPE);
		
		int viewportScale = 75;
		worldCamera = new OrthographicCamera(g().getWidth()/viewportScale, g().getHeight()/viewportScale);
		
		
		// Create our new rendering system
		renderingSystem = new RenderingSystem(worldBatch, client().world(), worldCamera, resizeSignal);
		engine().addSystem(renderingSystem);
		
		engine().addEntity(createPlayerEntity(engine(), 0, 0));
		//engine().addEntity(createPlayerEntity(engine(), 1, 0));
		//engine().addEntity(createPlayerEntity(engine(), 4, 1));
		
	}
	
	private Entity createPlayerEntity(PooledEngine engine, int x, int y) {
		Entity entity = engine.createEntity();
		
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		TypeComponent type = engine.createComponent(TypeComponent.class).set(EntityType.PLAYER);
		LocationComponent location = engine.createComponent(LocationComponent.class).setLocation(x, y);
		TextureComponent texture = engine.createComponent(TextureComponent.class).set(playerImgRegion);
		TransformComponent transform = engine.createComponent(TransformComponent.class);
		
		entity.add(player)
		.add(type)
		.add(location)
		.add(texture)
		.add(transform);
		
		return entity;
	}
	
	@Override
	public void exit() {
		//Gdx.input.setInputProcessor(null);
		engine().removeSystem(renderingSystem);
		engine().removeAllEntities();
		worldBatch.dispose();
		font.dispose();
	}
	
	@Override
	public void Update() {
		if(KEY_ESCAPE.ifPressed()) {
			Gdx.app.exit();
		}
	}
	
}
