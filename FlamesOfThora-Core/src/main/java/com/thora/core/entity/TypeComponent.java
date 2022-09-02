package com.thora.core.entity;

import com.badlogic.ashley.core.Component;

public class TypeComponent implements Component {
	
	public EntityType type;

	public final EntityType getType() {
		return type;
	}

	public final TypeComponent setType(EntityType type) {
		this.type = type;
		return this;
	}
	
	public final TypeComponent set(EntityType type) {
		return setType(type);
	}
	
}
