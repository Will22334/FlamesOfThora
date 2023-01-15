package com.thora.core.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.thora.core.world.Location;

public class BasicTypedWorldEntity extends AbstractWorldEntity {
	
	public static String sanitizeName(@NonNull final EntityType type, @Nullable final String name) {
		if(name != null) {
			return name;
		}
		return type.getDefaultName();
	}
	
	private final EntityType type;
	protected String name;
	
	public BasicTypedWorldEntity(final int id, @NonNull final EntityType type, @Nullable final String name, @NonNull final Location loc) {
		super(id, loc);
		this.type = type;
		this.name = sanitizeName(type, name);
	}
	
	@Override
	public String getName() {
		return name;
	}
	
	@Override
	public final EntityType getEntityType() {
		return type;
	}
	
}
