package com.thora.core.entity;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public enum EntityType {
	
	HUMAN_MALE("Human"),
	HUMAN_FEMALE("Human");
	
	private static final EntityType[] all = values();
	private static final List<EntityType> allList = Arrays.asList(all);
	
	public static final List<EntityType> getAll() {
		return allList;
	}
	
	public static final Stream<EntityType> all() {
		return getAll().stream();
	}
	
	private final String defaultName;
	
	private EntityType(final String defaultName) {
		this.defaultName = defaultName;
	}
	
	public final String getDefaultName() {
		return defaultName;
	}
	
}
