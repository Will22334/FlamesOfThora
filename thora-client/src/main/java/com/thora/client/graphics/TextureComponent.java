package com.thora.client.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class TextureComponent implements Component {
	
	private TextureRegion region = null;

	public final TextureRegion getRegion() {
		return region;
	}

	public final TextureComponent setRegion(TextureRegion region) {
		this.region = region;
		return this;
	}
	
	public final TextureComponent set(TextureRegion region) {
		return setRegion(region);
	}
	
}