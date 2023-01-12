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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thora.client.FlamesOfThoraClient;
import com.thora.client.sprite.SpriteSheet;
import com.thora.client.system.RenderingSystem;
import com.thora.core.world.Locatable;
import com.thora.core.world.Location;
import com.thora.core.world.Material;
import com.thora.core.world.Tile;
import com.thora.core.world.World;

public class WorldRenderer extends RenderingSystem {
	
	public World world;
	final SpriteBatch batch;
	protected ShapeRenderer shapeRend;
	
	private boolean tileBorders = true;
	private int viewRange = 16;
	
	private static final SpriteSheet tileSprites;
	public static final Color TILE_BORDER_COLOR = new Color(0f, 0f, 0f, .2f);
	
	private static final Map<Material,Texture> tileTextures;
	
	protected static final TextureRegion getTileTexture(Tile tile) {
		return getTileTexture(tile.getMaterial());
	}
	
//	private static final Texture getTileTexture(Material type) {
//		return tileTextures.get(type);
//	}
	
	private static final TextureRegion getTileTexture(Material type) {
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
	
	
	public WorldRenderer(FlamesOfThoraClient client, SpriteBatch batch, World world, Camera camera, Locatable focus, Signal<Dimension> resizeSignal,
			int priority) {
		super(client, batch, camera, focus, resizeSignal, priority);
		this.batch = batch;
		this.world = world;
		shapeRend = new ShapeRenderer();
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
	
	protected void drawTiles(World world, boolean borders) {
		drawTileTextures(world);
		if(borders) {
			drawTileBorders(world);
		}
	}
	
	private void drawTileTextures(World world) {
		world.surroundingTiles(getFocus(), (double)viewRange)
		.filter(t -> t.isInRange(getFocus(), (double)viewRange))
		.forEach(this::drawTileTexture);
	}
	
	private void drawTileBorders(World world) {
		batch.end();
		getCam().update();
		Gdx.gl.glEnable(GL11.GL_BLEND);
		Gdx.gl.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		Gdx.gl.glLineWidth(1f);
		shapeRend.setProjectionMatrix(getCam().combined);
		shapeRend.begin(ShapeRenderer.ShapeType.Line);
		
		world.surroundingTiles(getFocus(), (double)viewRange)
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
		TextureRegion t = getTileTexture(tile);
		Location loc = tile.getLocation();
		int width = t.getRegionWidth();
		int height = t.getRegionHeight();
		
		//float originX = width/2f;
		//float originY = height/2f;
		
//		batch.draw(t,
//			loc.getX(), loc.getY(),
//			TILE_TEXTURE_DRAW_RATIO, TILE_TEXTURE_DRAW_RATIO);
//		
//		batch.draw(t,
//				loc.getX() , loc.getY(),
//				0f, 0f,
//				width, height,
//				1f,
//				width, height,
//				0, 0,
//				width, height,
//				false, false);
		
			batch.draw(t,
					loc.getX(), loc.getY(),
					width / PPM, height / PPM);
		
	}
	
}
