package com.thora.client.world;


import java.util.EnumMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.util.Dimension;

import com.badlogic.ashley.signals.Signal;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thora.client.system.RenderingSystem;
import com.thora.core.world.ILocatable;
import com.thora.core.world.Location;
import com.thora.core.world.Tile;
import com.thora.core.world.Material;
import com.thora.core.world.AbstractWorld;

public class WorldRenderer extends RenderingSystem {
	
	private final AbstractWorld world;
	SpriteBatch batch;
	protected ShapeRenderer shapeRend;
	private boolean tileBorders = true;
	private int viewRange = 15;
	
	public static final Color TILE_BORDER_COLOR = new Color(0f, 0f, 0f, .2f);
	
	private static final Map<Material,Texture> tileTextures;
	
	protected static final Texture getTileTexture(Tile tile) {
		return getTileTexture(tile.getMaterial());
	}
	
	private static final Texture getTileTexture(Material type) {
		return tileTextures.get(type);
	}
	
	static {
		tileTextures = new EnumMap<>(Material.class);
		for(Material type: Material.all()) {
			Texture texture = new Texture("assets/tiles/" + type.getName().toLowerCase() + ".png");
			tileTextures.put(type, texture);
		}
	}
	
	
	public WorldRenderer(SpriteBatch batch, AbstractWorld world, Camera camera, ILocatable focus, Signal<Dimension> resizeSignal,
			int priority) {
		super(batch, camera, focus, resizeSignal, priority);
		this.batch = batch;
		this.world = world;
		shapeRend = new ShapeRenderer();
	}
	
	public void render() {
		
	}
	
	@Override
	public void update(float deltaTime) {
		//super.update(deltaTime);
		
		batch.begin();
		
		drawTiles(world, tileBorders);
		
		shapeRend.end();
		batch.end();
		
		drawEntities(world);
		
	}
	
	protected void drawTiles(AbstractWorld world, boolean borders) {
		drawTileTextures(world);
		if(borders) {
			drawTileBorders(world);
		}
	}
	
	private void drawTileTextures(AbstractWorld world) {
		world.surroundingTiles(getFocus(), viewRange)
		.forEach(this::drawTileTexture);
	}
	
	private void drawTileBorders(AbstractWorld world) {
		batch.end();
		getCam().update();
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(1f);
		shapeRend.setProjectionMatrix(getCam().combined);
		shapeRend.begin(ShapeRenderer.ShapeType.Line);
		
		world.surroundingTiles(getFocus(), viewRange )
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
	
	protected void drawTileBorder(Tile tile) {
		Location loc = tile.getLocation();
		shapeRend.setColor(TILE_BORDER_COLOR);
		
		shapeRend.rect(loc.getX(), loc.getY(),
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
				loc.getX(), loc.getY(),
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
	
}
