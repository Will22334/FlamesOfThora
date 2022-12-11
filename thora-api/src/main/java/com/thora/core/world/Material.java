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
	
	private static final Material[] nonVoidArray;
	private static final List<Material> nonVoidList;
	
	static {
		allArray = values();
		assert(allArray.length > 1) : Material.class.getSimpleName() + "[] total length should be >1 but is " + allArray.length;
		allList = Collections.unmodifiableList(Arrays.asList(allArray));
		
		nonVoidArray = new Material[allArray.length-1];
		for(int i=1;i<allArray.length;++i) {
			nonVoidArray[i-1] = allArray[i];
		}
		nonVoidList = Collections.unmodifiableList(Arrays.asList(nonVoidArray));
	}
	
	public static final List<Material> all() {
		return allList;
	}
	
	public static final Stream<Material> stream() {
		return all().stream();
	}
	
	public static final List<Material> allNonVoid() {
		return nonVoidList;
	}
	
	public static final Stream<Material> streamNonVoid() {
		return allNonVoid().stream();
	}
	
	public static final Material random(Random rand) {
		return allArray[rand.nextInt(allArray.length)];
	}
	
	public static final Material randomNonVoid(Random rand) {
		return nonVoidArray[rand.nextInt(nonVoidArray.length)];
	}
	
	public static final Material get(int id) {
		return allArray[id];
	}
	
	public final int getID() {
		return ordinal();
	}
	
	public final String getName() {
		return toString();
	}
	
	private Material() {
		
	}
}
