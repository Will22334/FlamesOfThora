package com.thora.client.sprite;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Animation2D implements ApplicationListener {

	public Animation<TextureRegion> animation; // Must declare frame type (TextureRegion)
		
	private SpriteSheet sheet;

	private float stateTime;
	
	SpriteBatch batch;

	
	public Animation2D(SpriteSheet image) {
		
		this.sheet = image;
		createAnmation(sheet.getIndexedImages());
		
	}
	
	public Animation2D(Texture t, int rows, int columns) {
		
		sheet = new SpriteSheet(t, rows, columns);
		createAnmation(sheet.getIndexedImages());
		
	}
	
	
	
	private void createAnmation(TextureRegion[] indexedSpriteSheet) {
		
		animation = new Animation<TextureRegion>(0.025f, indexedSpriteSheet);
		
		// Instantiate a SpriteBatch for drawing and reset the elapsed animation
		// time to 0
		batch = new SpriteBatch();
		stateTime = 0f;
	}
	
	@Override
	public void create() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void render() {
		// TODO Auto-generated method stub
		stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

		// Get current frame of animation for the current stateTime
		TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
		batch.begin();
		batch.draw(currentFrame, 50, 50); // Draw current frame at (50, 50)
		batch.end();
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		sheet.dispose();
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
}
