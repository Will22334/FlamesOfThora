package com.thora.client.sprite;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class SpriteSheet {

	private Texture spriteSheet;
	
	private int rows, columns;
	
	private TextureRegion[] indexedSpriteSheet;
	
	private TextureRegion[][] splitImage;
	
	public SpriteSheet(Texture spriteSheet, int rows, int columns) {
		
		this.spriteSheet = spriteSheet;
		this.rows = rows;
		this.columns = columns;
		
		splitSheet();
		
		indexSheet();
		
	}
	
	private void splitSheet() {
		
		// Use the split utility method to create a 2D array of TextureRegions. This is
		// possible because this sprite sheet contains frames of equal size and they are
		// all aligned.
		splitImage = TextureRegion.split(spriteSheet,
				spriteSheet.getWidth() / rows,
				spriteSheet.getHeight() / columns);
	}
	
	private TextureRegion[] indexSheet() {
		
		// Place the regions into a 1D array in the correct order, starting from the top
		// left, going across first. The Animation constructor requires a 1D array.
		indexedSpriteSheet = new TextureRegion[rows * columns];
		int index = 0;
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				indexedSpriteSheet[index++] = splitImage[j][i];
			}
		}
		
		return indexedSpriteSheet;

	}
	
	public TextureRegion[] getIndexedImages() {
		return indexedSpriteSheet;
	}
	
	public TextureRegion getSprite(int index) {
		return indexedSpriteSheet[index];
	}
	
	public Texture getTexture() {
		return spriteSheet;
	}
	
	public void dispose() {
		spriteSheet.dispose();
		
	}
}
