package com.thora.core.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.thora.core.world.Location;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.MoveEventComponent;

public class MoveSystem extends IteratingSystem {
	
	public static final Family FAMILY = Family.all(LocationComponent.class, MoveEventComponent.class).get();
	
	public MoveSystem(int priority) {
		super(FAMILY, priority);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//MoveEvent is valid if entity still has MoveEventComponent
		MoveEventComponent event = entity.remove(MoveEventComponent.class);
		Location point = LocationComponent.MAPPER.get(entity).getLocation();
		point.shift(event.v);
	}
	
}
