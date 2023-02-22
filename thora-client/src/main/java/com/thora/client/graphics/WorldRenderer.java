package com.thora.client.graphics;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thora.core.world.Locatable;
import com.thora.core.world.World;

public interface WorldRenderer {
	
	public void render(World world, Camera camera, Locatable center, Batch worldBatch, ShapeRenderer shapeBatch, boolean tileBorders);
	
}
