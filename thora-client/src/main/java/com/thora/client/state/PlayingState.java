package com.thora.client.state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.ashley.signals.Listener;
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
import com.thora.client.FlamesOfThoraClient;
import com.thora.client.graphics.MultiTextureComponent;
import com.thora.client.graphics.TextureComponent;
import com.thora.client.graphics.TransformComponent;
import com.thora.client.input.InputHandler;
import com.thora.client.input.InputHandler.KeyBinding;
import com.thora.client.input.InputHandler.KeyRecord;
import com.thora.client.input.InputListener;
import com.thora.client.input.Key;
import com.thora.client.system.MoveSystem;
import com.thora.client.system.MoveValidationSystem;
import com.thora.client.world.WorldRenderer;
import com.thora.core.HasLogger;
import com.thora.core.entity.EntityType;
import com.thora.core.entity.PlayerComponent;
import com.thora.core.entity.TypeComponent;
import com.thora.core.world.ILocatable;
import com.thora.core.world.Location;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.MovableComponent;
import com.thora.core.world.MoveEvent;
import com.thora.core.world.MoveRequestComponent;
import com.thora.core.world.WeakVectorLocation;

public class PlayingState extends GameState implements HasLogger {
	
	private static final Logger logger =  LogManager.getLogger(PlayingState.class);
	
	public static final double WALK_SPEED_TPS = 10f;
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
	
	private final InputHandler in = new InputHandler();
	private final InputListener inputListener = new InputListener(in);
	
	public PlayingState(FlamesOfThoraClient client, String name, int id) {
		super(client, name, id);
	}
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	@Override
	public void onCreate() {
		this.appSize = new Dimension(g().getWidth(), g().getHeight());
		logger().trace("Created Playing State!");
	}
	
	private Matrix4 uiMatrix = new Matrix4();
	private static final Color COLOR_OFF_WHITE = new Color(1f, 1f, 1f, .5f);
	
	private final Vector2 v = new Vector2();
	
	//Various tasks that should be completed on the render portion of the game loop.
	@Override
	public void render(float dt) {
		Gdx.gl.glClearColor( 0, 0, 0, 1 );
		Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT );
		
		//Updates the entity system. 
		//engine().update(delta);
		
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
		
		//update(dt);
	}
	
	//Various tasks that should be completed on the update portion of the game loop.
	@Override
	public void update(float dt) {
		
		//Update the Delta.
		updateLocalDelta(dt);
		
		//Handle Key Events for Keyboard Keys registered in this state.
		handleInput();
		
		engine().update(delta);
		
		//Update the camera.
		//worldCamera.update();
		
	}
	
	@Override
	public void onPause() {
		
	}
	
	@Override
	public void onResume() {
		
	}
	
	public InputListener getInputlistener() {
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
		
		in.bindKey(EXIT_BINDING, Keys.ESCAPE);
		in.bindKey(SHOW_GRID_BIDING, Keys.G);
		
		//inputHandler.RegisterKey(KEY_ESCAPE);
		in.RegisterKey(KEY_UP);
		in.RegisterKey(KEY_DOWN);
		in.RegisterKey(KEY_LEFT);
		in.RegisterKey(KEY_RIGHT);
		in.RegisterKey(KEY_G);
		
		
		Location spawn = new WeakVectorLocation<>(client().world(), 50, 50);
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
	
	private Entity createPlayerEntity(PooledEngine engine, ILocatable loc) {
		return createPlayerEntity(engine, loc.getLocation().getX(), loc.getLocation().getY());
	}
	
	private Entity createPlayerEntity(PooledEngine engine, int x, int y) {
		Entity entity = engine.createEntity();
		
		PlayerComponent player = engine.createComponent(PlayerComponent.class);
		
		TypeComponent type = engine.createComponent(TypeComponent.class).set(EntityType.PLAYER);
		LocationComponent location = engine.createComponent(LocationComponent.class).setLocation(x, y);
		MovableComponent movable = engine.createComponent(MovableComponent.class);
		
		movable.signal.add(new Listener<MoveEvent>() {
			@Override
			public void receive(Signal<MoveEvent> signal, MoveEvent event) {
				location.getLocation().shift(event.dx(), event.dy());
				worldCamera.position.add(event.dx(), event.dy(), 0);
				worldCamera.update();
			}
		});
		
		TextureComponent fronttexture = engine.createComponent(TextureComponent.class).set(playerImgRegion);
		TextureComponent backTexture = engine.createComponent(TextureComponent.class).set(playerImgBackRegion);
		
		MultiTextureComponent textures = new MultiTextureComponent();
		
		textures.addTextureComponent(fronttexture, 0);
		textures.addTextureComponent(backTexture, 1);	
		
		
		TransformComponent transform = engine.createComponent(TransformComponent.class);
		
		entity.add(player)
		.add(type)
		.add(location)
		.add(movable)
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
	
	private KeyBinding EXIT_BINDING = new KeyBinding() {
		@Override
		public boolean onPress(KeyRecord key) {
			Gdx.app.exit();
			return true;
		}
	};
	
	private KeyBinding SHOW_GRID_BIDING = new KeyBinding() {
		@Override
		public boolean onRelease(KeyRecord key) {
			logger().debug("Last Resized at: {}", lastGridToggleTime);
			logger().debug("Toggling Grid");
			worldRenderer.toggleBorders();
			lastGridToggleTime = delta;
			return true;
		}
	};
	
	private void handleInput() {
		
		//TODO Instead of polling input every frame, have a State specific InputProcesser implement input logic.
		
		
		Location loc = player.getComponent(LocationComponent.class).getLocation();
		
		//		if(KEY_ESCAPE.ifPressed()) {
		//			
		//			Gdx.app.exit();
		//			
		//		}
		
		//Toggles the Grid. Modify Time offset for better responsiveness.
		//		if(KEY_G.ifPressed()) {
		//			
		//			if((lastGridToggleTime + GRID_TOGGLE_LIMIT_DURATION) < delta) {
		//				
		//				this.log("Last Resized at: " + lastGridToggleTime);
		//				
		//				this.log("Toggling Grid");
		//				
		//				worldRenderer.toggleBorders();
		//				
		//				lastGridToggleTime = delta;
		//				
		//			}
		//			
		//		}
		
		long time = System.currentTimeMillis();
		if(time > lastWalkTime + WALK_TILE_DURATION) {
			
			if(in.isKeyDown(Keys.UP)) {
				walk(0, 1);
			}
			if(in.isKeyDown(Keys.DOWN)) {
				walk(0, -1);
			}
			if(in.isKeyDown(Keys.LEFT)) {
				walk(-1, 0);
			}
			if(in.isKeyDown(Keys.RIGHT)) {
				walk(1, 0);
			}
			
			//			if(KEY_UP.ifPressed()) {
			//				walk(0, 1);
			//			}
			//			
			//			if(KEY_DOWN.ifPressed()) {
			//				walk(0, -1);
			//			}
			//			
			//			if(KEY_LEFT.ifPressed()) {
			//				walk(-1, 0);
			//			}
			//			
			//			if(KEY_RIGHT.ifPressed()) {
			//				walk(1, 0);
			//			}
			
			if(!v.isZero()) {
				player.add(engine().createComponent(MoveRequestComponent.class).set(v.cpy()));
				
				//loc.shift((int)v.x, (int)v.y);
				//worldCamera.position.add(v.x, v.y, 0);
				v.setZero();
				lastWalkTime = time;
				//worldCamera.update();
			}
		}
		
	}
	
	protected void walk(int dx, int dy) {
		v.add(dx, dy);
		if(dy > 0) {
			player.getComponent(MultiTextureComponent.class).setActiveComponent(1);
		} else if(dy < 0) {
			player.getComponent(MultiTextureComponent.class).setActiveComponent(0);
		}
	}
	
	/* Updates the local delta time. 
	 *(The time since the playing state was created and the first update was ran)
	 */
	private void updateLocalDelta(float dt) {
		//delta += Gdx.app.getGraphics().getDeltaTime();
		delta += dt;
	}
	
	@Override
	public void setName(String name) {
		
		//Sets the state name to Playing State(Hard Coded for utility purposes)
		
		this.setName("Playing State");
		
	}
	
	
}
