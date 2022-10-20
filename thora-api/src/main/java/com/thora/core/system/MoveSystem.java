package com.thora.core.system;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.thora.core.world.Location;
import com.thora.core.world.LocationComponent;
import com.thora.core.world.MovableComponent;
import com.thora.core.world.MoveEvent;
import com.thora.core.world.MoveRequestComponent;

public class MoveSystem extends IteratingSystem {
	
	public static final Family FAMILY = Family.all(LocationComponent.class, MoveRequestComponent.class).get();
	
	public MoveSystem(int priority) {
		super(FAMILY, priority);
	}

	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		//MoveEvent is valid if entity still has MoveEventComponent
		
		MoveRequestComponent req = entity.getComponent(MoveRequestComponent.class);
		Location newPoint = LocationComponent.MAPPER.get(entity).getLocation();
		Location oldPoint = newPoint.clone();
		
		newPoint.shift(req.v);
		
		entity.remove(MoveRequestComponent.class);
		
		MovableComponent mc = MovableComponent.MAPPER.get(entity);
		if(mc != null) {
			mc.dispatch(new MoveEvent(oldPoint, newPoint));
		}
	}
	
}
