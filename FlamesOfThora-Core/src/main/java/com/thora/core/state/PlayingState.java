package com.thora.core.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.thora.core.Console;
import com.thora.core.FlamesOfThora;
import com.thora.core.entity.EntityType;
import com.thora.core.entity.PlayerComponent;
import com.thora.core.entity.TypeComponent;
import com.thora.core.graphics.MultiTextureComponent;
import com.thora.core.graphics.TextureComponent;
import com.thora.core.graphics.TransformComponent;
import com.thora.core.input.InputHandler;
import com.thora.core.input.InputListener;
import com.thora.core.input.Key;
import com.thora.core.system.MoveSystem;
import com.thora.core.system.MoveValidationSystem;
import com.thora.core.world.Locatable;
import com.thora.core.world.Location;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.MovableComponent;
import com.thora.core.world.MoveEventComponent;
import com.thora.core.world.WorldRenderer;

public class PlayingState extends GameState implements Console {
	
	private static final Logger logger =  LogManager.getLogger(PlayingState.class);
	
	private static final InputHandler inputHandler = new InputHandler();
	private static final InputListener inputListener = new InputListener(inputHandler);
	
	public static final double WALK_SPEED_TPS = 12f;
	public static final long WALK_TILE_DURATION = (long) (1000 / WALK_SPEED_TPS);
	
	public static final double GRID_TOGGLE_SPEED_TPS = 7.5f;
	public static final long GRID_TOGGLE_LIMIT_DURATION = (long) (8 / GRID_TOGGLE_SPEED_TPS);
	
	private static final Key KEY_ESCAPE = new Key(Keys.ESCAPE);
	private static final Key KEY_UP = new Key(Keys.UP);
	private static final Key KEY_DOWN = new Key(Keys.DOWN);
	private static final Key KEY_LEFT = new Key(Keys.LEFT);
	private static final Key KEY_RIGHT = new Key(Keys.RIGHT);
	private static final Key KEY_G = new Key(Keys.G);
	
	private WorldRenderer worldRenderer;
	
	private float delta;
	private float lastGridToggleTime;
	
	public static final Matrix4 NATIVE_MATRIX = new Matrix4();
	
	private OrthographicCamera worldCamera;
	private SpriteBatch worldBatch;
	private SpriteBatch hudBatch;
	private SpriteBatch entityBatch;
	private ShapeRenderer shapeRend;
	
	private BitmapFont font;
	Texture playerImg;
	Texture playerImgBack;
	
	TextureRegion playerImgRegion;
	TextureRegion playerImgBackRegion;
	
	private Entity player;
	private long lastWalkTime;
	
	/**
	 * A cached window size should be handled in Client instances.
	 */
	private Dimension appSize;
	
	private Signal<Dimension> resizeSignal = new Signal<>();
	
	public PlayingState(FlamesOfThora client, String name, int id) {
		super(client, name, id);
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	@Override
	public void onCreate() {
		this.appSize = new Dimension(g().getWidth(), g().getHeight());
		this.log("Created Playing State!");
	}
	
	private Matrix4 uiMatrix = new Matrix4();
	private static final Color COLOR_OFF_WHITE = new Color(1f, 1f, 1f, .5f);
	
	private final Vector2 v = new Vector2();
	
	//Various tasks that should be completed on the render portion of the game loop.
	@Override
	public void onRender() {
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		//Updates the entity system. 
		engine().update(delta);
		
		float width = g().getWidth();
		float height = g().getHeight();
		
		//worldCamera.update();
		uiMatrix.set(worldCamera.combined);
		uiMatrix.setToOrtho2D(0, 0, width, height);
		
		//batch.setTransformMatrix(hudCamera.combined);
		hudBatch.setProjectionMatrix(uiMatrix);
		hudBatch.begin();
		
		Location loc = player.getComponent(LocationComponent.class).getLocation();
		
		/*
		 * FPS COUNTER
		 */
		font.setColor(Color.RED);
		String msg = String.format("FPS: %s\t(%s,%s)\n%s", g().getFramesPerSecond(), Gdx.input.getX(), Gdx.input.getY(), loc);
		font.draw(hudBatch, msg, 0, height);
		
		
		hudBatch.end();
		
		shapeRend.setProjectionMatrix(uiMatrix);
		shapeRend.begin(ShapeRenderer.ShapeType.Line);
		Gdx.gl.glEnable(GL11.GL_BLEND);
		shapeRend.setColor(COLOR_OFF_WHITE);
		shapeRend.line(width/2, 0, width/2, height);
		shapeRend.line(0, height/2, width, height/2);
		Gdx.gl.glLineWidth(1f);
		shapeRend.end();
		
		Update();
	}
	
	//Various tasks that should be completed on the update portion of the game loop.
	@Override
	public void Update() {
		
		//Update the Delta.
		updateLocalDelta();
		
		//Handle Key Events for Keyboard Keys registered in this state.
		handleInput();
		
		//Update the camera.
		//worldCamera.update();
		
	}
	
	@Override
	public void onPause() {
		
	}
	
	@Override
	public void onResume() {
		
	}
	
	public static InputListener getInputlistener() {
		return inputListener;
	}
	
	@Override
	public void onResize(int width, int height) {
		logger().debug("ON_RESIZE: [{},{}] -> [{},{}]", appSize.getWidth(), appSize.getHeight(), width, height);
		worldCamera.update();
		appSize.setSize(width, height);
		
		resizeSignal.dispatch(appSize);
		//worldCamera.setToOrtho(false, g().getWidth()/viewportScale, g().getHeight()/viewportScale);
		worldCamera.update();
		//		hudBatch.dispose();
		//		worldBatch.dispose();
		//		font.dispose();
		//		hudBatch = new SpriteBatch();
		//		worldBatch = new SpriteBatch();
		//		font = new BitmapFont();
	}
	
	float viewportScale = 30f;
	
	@Override
	public void enter() {
		hudBatch = new SpriteBatch();
		worldBatch = new SpriteBatch();
		shapeRend = new ShapeRenderer();
		font = new BitmapFont();
		
		//Player Images
		playerImg = new Texture("assets/player.png");
		playerImgBack = new Texture("assets/playerbackdetails.png");
		playerImgRegion = new TextureRegion(playerImg);
		playerImgBackRegion = new TextureRegion(playerImgBack);
		
		Gdx.input.setInputProcessor(inputListener);
		
		inputHandler.RegisterKey(KEY_ESCAPE);
		inputHandler.RegisterKey(KEY_UP);
		inputHandler.RegisterKey(KEY_DOWN);
		inputHandler.RegisterKey(KEY_LEFT);
		inputHandler.RegisterKey(KEY_RIGHT);
		inputHandler.RegisterKey(KEY_G);
		
		
		Location spawn = new Location(50, 50);
		player = createPlayerEntity(engine(), spawn);
		engine().addEntity(player);
		
		
		worldCamera = new OrthographicCamera(g().getWidth()/viewportScale, g().getHeight()/viewportScale);
		worldCamera.position.set(spawn.getX(), spawn.getY(), 0);
		
		
		worldRenderer = new WorldRenderer(worldBatch, client().world(), worldCamera, player.getComponent(LocationComponent.class),
				resizeSignal, 100);
		
		engine().addSystem(worldRenderer);
		
		
		
		engine().addSystem(new MoveValidationSystem(10));
		
		engine().addSystem(new MoveSystem(20));
		
	}
	
	private Entity createPlayerEntity(PooledEngine engine, Locatable loc) {
		return createPlayerEntity(engine, loc.getLocation().getX(), loc.getLocation().getY());
	}
	
	private Entity createPlayerEntity(PooledEngine engine, int x, int y) {
		Entity entity = engine.createEntity();
		
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		
		TypeComponent type = engine.createComponent(TypeComponent.class).set(EntityType.PLAYER);
		LocationComponent location = engine.createComponent(LocationComponent.class).setLocation(x, y);
		
		TextureComponent fronttexture = engine.createComponent(TextureComponent.class).set(playerImgRegion);
		TextureComponent backTexture = engine.createComponent(TextureComponent.class).set(playerImgBackRegion);
		
		MultiTextureComponent textures = new MultiTextureComponent();
		
		textures.addTextureComponent(fronttexture, 0);
		textures.addTextureComponent(backTexture, 1);	
		
		
		TransformComponent transform = engine.createComponent(TransformComponent.class);
		
		entity.add(player)
		.add(type)
		.add(location)
		.add(MovableComponent.INSTANCE)
		.add(fronttexture)
		.add(textures)
		.add(transform);
		
		return entity;
	}
	
	@Override
	public void exit() {
		//Gdx.input.setInputProcessor(null);
		engine().removeSystem(worldRenderer);
		engine().removeAllEntities();
		worldBatch.dispose();
		hudBatch.dispose();
		shapeRend.dispose();
		font.dispose();
	}
	
	public SpriteBatch getEntityBatch() {
		return entityBatch;
	}
	
	public void setEntityBatch(SpriteBatch entityBatch) {
		this.entityBatch = entityBatch;
	}
	
	private void handleInput() {
		
		//TODO Instead of polling input every frame, have a State specific InputProcesser implement input logic.
		
		
		Location loc = player.getComponent(LocationComponent.class).getLocation();
		
		if(KEY_ESCAPE.ifPressed()) {
			
			Gdx.app.exit();
			
		}
		
		//Toggles the Grid. Modify Time offset for better responsiveness.
		if(KEY_G.ifPressed()) {
			
			if((lastGridToggleTime + GRID_TOGGLE_LIMIT_DURATION) < delta) {
				
				this.log("Last Resized at: " + lastGridToggleTime);
				
				this.log("Toggling Grid");
				
				worldRenderer.toggleBorders();
				
				lastGridToggleTime = delta;
				
			}
			
		}
		
		long time = System.currentTimeMillis();
		if(time > lastWalkTime + WALK_TILE_DURATION) {
			
			if(KEY_UP.ifPressed()) {
				v.add(0, 1);
				player.getComponent(MultiTextureComponent.class).setActiveComponent(1);
			}
			
			if(KEY_DOWN.ifPressed()) {
				player.getComponent(MultiTextureComponent.class).setActiveComponent(0);
				v.add(0, -1);
			}
			
			if(KEY_LEFT.ifPressed()) {
				v.add(-1, 0);
			}
			
			if(KEY_RIGHT.ifPressed()) {
				v.add(1, 0);
			}
			
			if(!v.isZero()) {
				player.add(engine().createComponent(MoveEventComponent.class).set(v));
				
				loc.shift((int)v.x, (int)v.y);
				worldCamera.position.add(v.x, v.y, 0);
				v.setZero();
				lastWalkTime = time;
				worldCamera.update();
			}
		}
		
	}
	
	/* Updates the local delta time. 
	 *(The time since the playing state was created and the first update was ran)
	 */
	private void updateLocalDelta() {
		
		delta += Gdx.app.getGraphics().getDeltaTime();
	}
	
	@Override
	public void setName(String name) {
		
		//Sets the state name to Playing State(Hard Coded for utility purposes)
		
		this.setName("Playing State");
		
	}
	
	
}
