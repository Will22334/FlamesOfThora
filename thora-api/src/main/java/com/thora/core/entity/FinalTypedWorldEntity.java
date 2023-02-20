package com.thora.core.entity;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import com.thora.core.world.Location;

public class FinalTypedWorldEntity extends AbstractWorldEntity {
	
	public static String sanitizeName(@NonNull final EntityType type, @Nullable final String name) {
		if(name != null) {
			return name;
		}
		return type.getDefaultName();
	}
	
	private final EntityType type;
	protected String name;
	
	public FinalTypedWorldEntity(final int id, @NonNull final String name, @NonNull final EntityType type, @NonNull final Location loc) {
		super(id, name, loc);
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

	@Override
	public boolean setEntityType(final EntityType type) {
		return false;
	}
	
}
