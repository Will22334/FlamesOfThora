package com.thora.client.world;


import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thora.client.FlamesOfThoraClient;
import com.thora.client.NameComponent;
import com.thora.client.graphics.MultiTextureComponent;
import com.thora.client.graphics.TextureComponent;
import com.thora.client.graphics.TransformComponent;
import com.thora.client.sprite.SpriteSheet;
import com.thora.client.state.PlayingState;
import com.thora.client.system.RenderingSystem;
import com.thora.core.world.Locatable;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.Material;
import com.thora.core.world.Tile;
import com.thora.core.world.World;

public class OldWorldRendererSystem extends RenderingSystem {
	
	public World world;
	protected ShapeRenderer shapeRend;
	
	private boolean tileBorders = true;
	private double viewRange = 16d;
	
	private static final SpriteSheet tileSprites;
	public static final Color TILE_BORDER_COLOR = new Color(0f, 0f, 0f, .2f);
	
	private static final Map<Material,Texture> tileTextures;
	
	protected static final TextureRegion getTileSprite(Tile tile) {
		return getTileSprite(tile.getMaterial());
	}
	
	private static final TextureRegion getTileSprite(Material type) {
		return tileSprites.getSprite(type.ordinal());
	}
	
	static {
		tileTextures = new EnumMap<>(Material.class);
		for(Material type: Material.all()) {
			Texture texture = new Texture("assets/tiles/" + type.getName().toLowerCase() + ".png");
			tileTextures.put(type, texture);
		}
		tileSprites = new SpriteSheet(new Texture("assets/tile_sheet.png"), 8, 1);
	}
	
	
	public OldWorldRendererSystem(FlamesOfThoraClient client, SpriteBatch batch, World world, Camera camera, Locatable focus, Signal<Dimension> resizeSignal,
			int priority) {
		super(client, batch, camera, focus, resizeSignal, priority);
		this.world = world;
		shapeRend = new ShapeRenderer();
	}
	
	protected double getViewRange() {
		return viewRange;
	}
	
	protected void setViewRange(double r) {
		this.viewRange = r;
	}
	
	protected void scaleViewRange(double s) {
		this.viewRange *= s;
	}
	
	@Override
	public void update(float deltaTime) {
		
		batch.begin();
		
		
		drawTiles(world, tileBorders);
		
		
		shapeRend.end();
		batch.end();
		
		drawEntities(world);
		
	}
	
	protected void drawTiles(World world, boolean borders) {
		drawTileTextures(world);
		if(borders) {
			drawTileBorders(world);
		}
	}
	
	private void drawTileTextures(World world) {
		batch.enableBlending();
		world.surroundingTiles(getFocus(), getViewRange())
		.filter(t -> t.isInRange(getFocus(), getViewRange()))
		.forEach(this::drawTileSprite);
	}
	
	private void drawTileBorders(World world) {
		batch.end();
		Gdx.gl.glEnable(GL11.GL_BLEND);
		shapeRend.setColor(0f, 0f, 0f, .2f);
		Gdx.gl.glLineWidth(1f);
		shapeRend.setProjectionMatrix(getCam().combined);
		shapeRend.begin(ShapeRenderer.ShapeType.Line);
		
		world.surroundingTiles(getFocus(), getViewRange())
		.forEach(this::drawTileBorder);
		
		shapeRend.end();
		batch.begin();
	}
	
	public void toggleBorders() {
		
		if(tileBorders == false) {
			
			tileBorders = true;
			
		} else {
			
			tileBorders = false;
		}
		
	}
	
	protected void drawTileBorder(final Tile tile) {
		shapeRend.rect(tile.getX(), tile.getY(),
				TILE_TEXTURE_DRAW_RATIO, TILE_TEXTURE_DRAW_RATIO);
	}
	
	protected void drawTileSprite(Tile tile) {
		final TextureRegion t = getTileSprite(tile);
		
		batch.draw(t,
				tile.getX(), tile.getY(),
				1f, 1f);
		
	}
	
	@Override
	protected void drawEntities(World world) {
		getCam().position.set(focus.getX() + .5f, focus.getY() +.5f, 0f);
		getCam().update();
		batch.setProjectionMatrix(getCam().combined);
		
		batch.enableBlending();
		//getCam().update();
		batch.begin();
		
		final PlayingState plState = (PlayingState) client().States.getActiveState();
		final BitmapFont font = plState.font;
		
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
			
			//Draw texture
			batch.draw(texture,
					loc.getX() + (PPM - width)/PPM/2, loc.getY(),
					PixelsToMeters(width), PixelsToMeters(height));
			
		}
		batch.end();
		getRenderQueue().clear();
	}
	
}
