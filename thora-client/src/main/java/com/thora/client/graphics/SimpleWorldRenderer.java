package com.thora.client.graphics;

import java.util.EnumMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thora.client.sprite.SpriteSheet;
import com.thora.core.world.Locatable;
import com.thora.core.world.Material;
import com.thora.core.world.Tile;
import com.thora.core.world.World;

public class SimpleWorldRenderer implements WorldRenderer {
	
	private static final SpriteSheet tileSprites;
	public static final Color TILE_BORDER_COLOR = new Color(0f, 0f, 0f, .2f);
	
	private static final Map<Material,Texture> tileTextures;
	
	protected static final TextureRegion getTileSprite(final Tile tile) {
		return getTileSprite(tile.getMaterial());
	}
	
	private static final TextureRegion getTileSprite(final Material type) {
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
	
	@Override
	public void render(World world, Camera camera, Locatable center, Batch worldBatch, ShapeRenderer shapeBatch, boolean tileBorders) {
		
//		world.surroundingTiles(getFocus(), getViewRange())
//		.forEach(this::drawTileBorder);
		
	}
	
	protected void drawTiles(World world, Locatable center, Batch worldBatch, Tile tile) {
		world.surroundingTiles(center, 16d)
		.filter(t -> t.isInRange(center, 16d))
		.forEach(t -> drawTile(worldBatch, tile));
	}
	
	protected void drawTile(Batch worldBatch, Tile tile) {
		
	}
	
}
