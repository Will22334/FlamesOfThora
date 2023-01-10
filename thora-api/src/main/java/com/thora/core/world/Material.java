package com.thora.core.world;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public enum Material {
	
	VOID(),
	GRASS(),
	DIRT(),
	WATER(),
	SAND(),
	JUNGLE(),
	WOOD(),
	BRICK();
	
	private static final Material[] allArray;
	private static final List<Material> allList;
	
	private static final Material[] noVoidArray;
	private static final List<Material> noVoidList;
	
	static {
		allArray = values();
		assert(allArray.length > 1) : Material.class.getSimpleName() + "[] total length should be >1 but is " + allArray.length;
		allList = Collections.unmodifiableList(Arrays.asList(allArray));
		
		noVoidArray = new Material[allArray.length-1];
		for(int i=1; i<allArray.length; ++i) {
			noVoidArray[i-1] = allArray[i];
		}
		noVoidList = Collections.unmodifiableList(Arrays.asList(noVoidArray));
	}
	
	public static final List<Material> all() {
		return allList;
	}
	
	public static final Stream<Material> stream() {
		return all().stream();
	}
	
	public static final List<Material> allNoVoid() {
		return noVoidList;
	}
	
	public static final Stream<Material> streamNoVoid() {
		return allNoVoid().stream();
	}
	
	public static final Material random(Random rand) {
		return allArray[rand.nextInt(allArray.length)];
	}
	
	public static final Material randomNoVoid(Random rand) {
		return noVoidArray[rand.nextInt(noVoidArray.length)];
	}
	
	public static final Material get(int id) {
		return allArray[id];
	}
	
	private Material() {
		
	}
	
	public final int getID() {
		return ordinal();
	}
	
	public final String getName() {
		return toString();
	}
	
}
