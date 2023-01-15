package com.thora.core.system;

import java.util.Comparator;
import java.util.Objects;

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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.thora.core.graphics.MultiTextureComponent;
import com.thora.core.graphics.TextureComponent;
import com.thora.core.graphics.TransformComponent;
import com.thora.core.graphics.ZComparator;
import com.thora.core.world.Locatable;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.AbstractWorld;

public class RenderingSystem extends SortedIteratingSystem {
	
	protected static final float TILE_TEXTURE_SIZE = 300f;
	protected static final float PPM = 300f; // sets the amount of pixels each metre of box2d objects contains
	protected static final float TILE_TEXTURE_DRAW_RATIO = TILE_TEXTURE_SIZE / PPM;
	
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
	
	
	protected SpriteBatch batch; // a reference to our spritebatch
	
	protected final Listener<Dimension> resizeListener = new Listener<Dimension>() {
		@Override
		public void receive(Signal<Dimension> signal, Dimension newSize) {
			FRUSTUM_WIDTH = newSize.getWidth()/PPM;
			FRUSTUM_HEIGHT = newSize.getHeight()/PPM;
		}
	};
	
	// component mappers to get components from entities
	public static final ComponentMapper<LocationComponent> locationM = ComponentMapper.getFor(LocationComponent.class);
	public static final ComponentMapper<TextureComponent> textureM = ComponentMapper.getFor(TextureComponent.class);
	public static final ComponentMapper<MultiTextureComponent> multitextureM = ComponentMapper.getFor(MultiTextureComponent.class);
	public static final ComponentMapper<TransformComponent> transformM = ComponentMapper.getFor(TransformComponent.class);
	
	public static final Family FAMILY = Family.all(LocationComponent.class, TransformComponent.class, TextureComponent.class).get();
	
	/**
	 * TODO Move texture loading outside of static block so time of loading is predictable
	 * and IO exception handling can be changed.
	 */
	
	private Array<Entity> renderQueue; // an array used to allow sorting of images allowing us to draw images on top of each other
	protected Comparator<Entity> comparator; // a comparator to sort images based on the z position of the transfromComponent
	private Camera cam; // a reference to our camera
	
	protected Locatable focus;
	
	protected Signal<Dimension> resizeSignal;
	
	public RenderingSystem(SpriteBatch batch, Camera camera, Locatable focus,
			Signal<Dimension> resizeSignal, int priority) {
		// gets all entities with a TransofmComponent and TextureComponent
		super(FAMILY, new ZComparator(), priority);
		
		this.focus = Objects.requireNonNull(focus, "RenderSystem's focus object cannot be null!");
		this.comparator = new ZComparator();
		
		
		this.batch = batch;
		// create the array for sorting entities
		setRenderQueue(new Array<Entity>());
		
		// set up the camera to match our screen size
		setCam(camera);
		//cam.position.set(FRUSTUM_WIDTH / 2f, FRUSTUM_HEIGHT / 2f, 0);
		
		this.resizeSignal = resizeSignal;
		this.resizeSignal.add(resizeListener);
		
	}
	
	protected Locatable getFocus() {
		return focus;
	}
	
	@Override
	public void processEntity(Entity entity, float deltaTime) {
		getRenderQueue().add(entity);
	}
	
	public void update(float deltaTime) {
		//super.update(deltaTime);
		
		// sort the renderQueue based on z index
		//getRenderQueue().sort(comparator);
		//this.getEntities();
		
		// update camera and sprite batch
		getCam().position.set(focus.getLocation().getX() + .5f, focus.getLocation().getY() +.5f, 0f);
		getCam().update();
		batch.setProjectionMatrix(getCam().combined);
		Gdx.gl.glEnable(GL11.GL_BLEND);
		batch.enableBlending();
		getCam().update();
		batch.begin();
		
		
		// loop through each entity in our render queue
		for (Entity entity : getEntities()) {
			
			TransformComponent t = transformM.get(entity);
			if(t.isHidden) continue;
			
			LocationComponent loc = locationM.get(entity);
			TextureComponent tex = textureM.get(entity);
			MultiTextureComponent tex2 = multitextureM.get(entity);
		
			if (loc == null || tex == null) {
				continue;
			}
			TextureRegion texRegion = tex2.getRegion();
			
			
			float width = texRegion.getRegionWidth();
			float height = texRegion.getRegionHeight();
			
			batch.draw(tex2.getActiveComponent().getRegion(),
					loc.getX() + (PPM - width)/PPM/2, loc.getY(),
					PixelsToMeters(width), PixelsToMeters(height));
			
		}
		batch.end();
		getRenderQueue().clear();
		
	}

	// convenience method to get camera
	public Camera getCamera() {
		return getCam();
	}



	public Camera getCam() {
		return cam;
	}

	public void setCam(Camera cam) {
		this.cam = cam;
	}

	public Array<Entity> getRenderQueue() {
		return renderQueue;
	}

	public void setRenderQueue(Array<Entity> renderQueue) {
		this.renderQueue = renderQueue;
	}
	
	protected void drawEntities(AbstractWorld world) {
		getCam().update();
		batch.setProjectionMatrix(getCam().combined);
		getCam().position.set(focus.getX() + .5f, focus.getY() +.5f, 0f);
		
		batch.enableBlending();
		//getCam().update();
		batch.begin();
		
		// loop through each entity in our render queue
		for (Entity entity : this.getEntities()) {
			
			TransformComponent t = transformM.get(entity);
			if(t.isHidden) continue;
			
			LocationComponent loc = locationM.get(entity);
			TextureComponent tex = textureM.get(entity);
			MultiTextureComponent tex2 = multitextureM.get(entity);
			
			if (loc == null || tex == null) {
				continue;
			}
			TextureRegion texRegion = tex.getRegion();
			
			
			float width = texRegion.getRegionWidth();
			float height = texRegion.getRegionHeight();
			final TextureRegion texture = tex2.getActiveComponent().getRegion();
			
			batch.draw(texture,
					loc.getX() + (PPM - width)/PPM/2, loc.getY(),
					PixelsToMeters(width), PixelsToMeters(height));
			
		}
		batch.end();
		getRenderQueue().clear();
	}
	
}