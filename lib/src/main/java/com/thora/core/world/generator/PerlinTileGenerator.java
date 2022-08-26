package com.thora.core.world.generator;

import com.thora.core.world.TileGenerator;
import com.thora.core.world.TileType;

public class PerlinTileGenerator implements TileGenerator {
	
	public static final float DEFAULT_TURBULANCE = 20f;
	
	protected final J3DPerlinNoise noise;
	protected final float scale;
	
	protected final float freq;
	
	public PerlinTileGenerator(J3DPerlinNoise noise, float scale, float freq) {
		this.noise = noise;
		this.scale = scale;
		this.freq = freq;
	}
	
	public PerlinTileGenerator(int seed, float scale, float freq) {
		this(new J3DPerlinNoise(seed), scale, freq);
	}
	
	public PerlinTileGenerator(J3DPerlinNoise noise, float scale) {
		this(noise, scale, DEFAULT_TURBULANCE);
	}
	
	public PerlinTileGenerator(int seed, float scale) {
		this(new J3DPerlinNoise(seed), scale, DEFAULT_TURBULANCE);
	}
	
	@Override
	public TileType getTileType(int x, int y) {
		float sx = x / scale;
		float sy = y / scale;
		float val = noise.turbulence2(sx, sy, scale);
		return valueToType(val);
	}
	
	protected TileType valueToType(float val) {
		if(val > .12f)
			return TileType.GRASS;
		else if(val > .075f)
			return TileType.SAND;
		return TileType.WATER;
	}
	
}
