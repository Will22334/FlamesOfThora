package com.thora.core.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public enum TileType {
	
	VOID(),
	GRASS(),
	DIRT(),
	WATER(),
	SAND(),
	JUNGLE(),
	WOOD(),
	BRICK();
	
	private static final TileType[] arr = values();
	private static final List<TileType> all = Collections.unmodifiableList(Arrays.asList(arr));
	
	public static final List<TileType> all() {
		return all;
	}
	
	public static final Stream<TileType> stream() {
		return all().stream();
	}
	
	public static final TileType random(Random rand) {
		return arr[rand.nextInt(arr.length)];
	}
	
	public final int getID() {
		return ordinal();
	}
	
	public final String getName() {
		return toString();
	}
	
	private TileType() {
		
	}
}
