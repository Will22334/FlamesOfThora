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
import com.thora.core.world.Material;
import com.thora.core.world.Tile;
import com.thora.core.world.World;

public class WorldRenderer extends RenderingSystem {
	
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
	
	
	public WorldRenderer(FlamesOfThoraClient client, SpriteBatch batch, World world, Camera camera, Locatable focus, Signal<Dimension> resizeSignal,
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
	
}
