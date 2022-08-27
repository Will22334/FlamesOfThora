package com.thora.core.graphics;

import java.util.Comparator;
import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Listener;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.thora.core.world.Location;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.Tile;
import com.thora.core.world.TileType;
import com.thora.core.world.World;

public class RenderingSystem extends SortedIteratingSystem {
	
	static final float TILE_TEXTURE_SIZE = 300f;
	static final float PPM = 300f; // sets the amount of pixels each metre of box2d objects contains
	static final float TILE_TEXTURE_DRAW_RATIO = TILE_TEXTURE_SIZE / PPM;
	
	// this gets the height and width of our camera frustrum based off the width and height of the screen and our pixel per meter ratio
	public static float FRUSTUM_WIDTH = Gdx.graphics.getWidth()/PPM;
	public static float FRUSTUM_HEIGHT = Gdx.graphics.getHeight()/PPM;
	
	public static final float PIXELS_TO_METRES = 1.0f / PPM; // get the ratio for converting pixels to metres
	
	// static method to get screen width in metres
	private static Vector2 meterDimensions = new Vector2();
	private static Vector2 pixelDimensions = new Vector2();
	
	public static Vector2 getScreenSizeInMeters(){
		meterDimensions.set(Gdx.graphics.getWidth()*PIXELS_TO_METRES,
				Gdx.graphics.getHeight()*PIXELS_TO_METRES);
		return meterDimensions;
	}
	
	// static method to get screen size in pixels
	public static Vector2 getScreenSizeInPixesl(){
		pixelDimensions.set(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		return pixelDimensions;
	}
	
	// convenience method to convert pixels to meters
	public static float PixelsToMeters(float pixelValue){
		return pixelValue * PIXELS_TO_METRES;
	}
	
	private final Listener<Dimension> resizeListener = new Listener<Dimension>() {
		@Override
		public void receive(Signal<Dimension> signal, Dimension newSize) {
			FRUSTUM_WIDTH = newSize.getWidth()/PPM;
			FRUSTUM_HEIGHT = newSize.getHeight()/PPM;
		}
	};
	
	// component mappers to get components from entities
	private static final ComponentMapper<LocationComponent> locationM = ComponentMapper.getFor(LocationComponent.class);
	private static final ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
	private static final ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);
	
	public static final Family FAMILY = Family.all(TransformComponent.class, TextureComponent.class).get();
	
	/**
	 * TODO Move texture loading outside of static block so time of loading is predictable
	 * and IO exception handling can be changed.
	 */
	private static final Map<TileType,Texture> tileTextures;
	
	private static final Texture getTileTexture(Tile tile) {
		return getTileTexture(tile.getType());
	}
	
	private static final Texture getTileTexture(TileType type) {
		return tileTextures.get(type);
	}
	
	static {
		tileTextures = new EnumMap<>(TileType.class);
		for(TileType type: TileType.all()) {
			Texture texture = new Texture("assets/tiles/" + type.getName().toLowerCase() + ".png");
			tileTextures.put(type, texture);
		}
	}
	
	
	private final World world;
	private SpriteBatch batch; // a reference to our spritebatch
	private ShapeRenderer shapeRend;
	
	private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
	private Comparator<Entity> comparator; // a comparator to sort images based on the z position of the transfromComponent
	private Camera cam; // a reference to our camera
	
	private Signal<Dimension> resizeSignal;
	
	public RenderingSystem(SpriteBatch batch, World world, Camera camera, Signal<Dimension> resizeSignal) {
		// gets all entities with a TransofmComponent and TextureComponent
		super(Family.all(LocationComponent.class, TransformComponent.class, TextureComponent.class).get(), new ZComparator());
		
		this.world = world;
		this.comparator = new ZComparator();
		
		// create the array for sorting entities
		renderQueue = new Array<Entity>();
		
		this.batch = batch;  // set our batch to the one supplied in constructor
		
		// set up the camera to match our screen size
		cam = camera;
		shapeRend = new ShapeRenderer();
		//cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
		
		this.resizeSignal = resizeSignal;
		this.resizeSignal.add(resizeListener);
		
	}
	
	public static final Color TILE_BORDER_COLOR = new Color(0f, 0f, 0f, .2f);
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		renderQueue.add(entity);
	}
	
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		
		// sort the renderQueue based on z index
		renderQueue.sort(comparator);
		
		// update camera and sprite batch
		cam.update();
		batch.setProjectionMatrix(cam.combined);
		Gdx.gl.glEnable(GL11.GL_BLEND);
		batch.enableBlending();
		cam.update();
		batch.begin();
		
		drawTiles(world);
		
		// loop through each entity in our render queue
		for (Entity entity : renderQueue) {
			TransformComponent t = transformM.get(entity);
			if(t.isHidden) continue;
			
			LocationComponent loc = locationM.get(entity);
			TextureComponent tex = textureM.get(entity);
			
			if (loc == null || tex == null) {
				continue;
			}
			TextureRegion texRegion = tex.getRegion();
			
			
			float width = texRegion.getRegionWidth();
			float height = texRegion.getRegionHeight();
			
			//			batch.draw(texRegion,
			//					loc.getX() - originX, loc.getY() - originY,
			//					originX, originY,
			//					width, height,
			//					PixelsToMeters(t.scale.x), PixelsToMeters(t.scale.y),
			//					t.rotation);
			
			
			float sc = (PPM - width) / PPM;
			
			//batch.setColor(1f, 1f, 1f, 1f);
			
			batch.draw(tex.getRegion(),
					loc.getX() -.25f, loc.getY() -.25f,
					width / PPM, height / PPM);
			
		}
		
		shapeRend.end();
		batch.end();
		renderQueue.clear();
		
	}
	
	protected void drawTiles(World world) {
		drawTileTextures(world);
		
		drawTileBorders(world);
		
	}
	
	private void drawTileTextures(World world) {
		world.surroundingTiles(new Location(0,0), 25)
		.forEach(this::drawTileTexture);
	}
	
	private void drawTileBorders(World world) {
		batch.end();
		cam.update();
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(1f);
		shapeRend.setProjectionMatrix(cam.combined);
		shapeRend.begin(ShapeRenderer.ShapeType.Line);
		
		world.surroundingTiles(new Location(0,0), 25)
		.forEach(this::drawTileBorder);
		
		shapeRend.end();
		batch.begin();
	}
	
	protected void drawTileBorder(Tile tile) {
		Location loc = tile.getLocation();
		shapeRend.setColor(TILE_BORDER_COLOR);
		
		shapeRend.rect(loc.getX() -.25f, loc.getY() -.25f,
				TILE_TEXTURE_DRAW_RATIO, TILE_TEXTURE_DRAW_RATIO);
	}
	
	protected void drawTileTexture(Tile tile) {
		Texture t = getTileTexture(tile);
		Location loc = tile.getLocation();
		int width = t.getWidth();
		int height = t.getHeight();
		
		//float originX = width/2f;
		//float originY = height/2f;
		
		batch.draw(t,
				loc.getX() -.25f, loc.getY() -.25f,
				TILE_TEXTURE_DRAW_RATIO, TILE_TEXTURE_DRAW_RATIO);
		
		//		batch.draw(t,
		//				loc.getX() , loc.getY(),
		//				0f, 0f,
		//				width, height,
		//				1f,
		//				width, height,
		//				0, 0,
		//				width, height,
		//				false, false);
		
		//		batch.draw(t,
		//				loc.getX(), loc.getY(),
		//				width / PPM, height / PPM,
		//				1, 1,
		//				1, 1,
		//				0f,
		//				priority, priority,
		//				(int)width, (int)height,
		//				false, false);
	}
	
	// convenience method to get camera
	public Camera getCamera() {
		return cam;
	}
}