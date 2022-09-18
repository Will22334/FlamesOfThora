package com.thora.core.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.thora.core.graphics.CameraComponent;

public class CameraSystem extends IteratingSystem {
	
	public static final Family FAMILY = Family.all(CameraComponent.class).get();
	
	public CameraSystem(int priority) {
		super(FAMILY, priority);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
	}
	
}
