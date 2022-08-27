package com.thora.core.graphics;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class ZComparator implements Comparator<Entity> {
	
	private static final ComponentMapper<TransformComponent> locMapper = ComponentMapper.getFor(TransformComponent.class);
	
	@Override
	public int compare(Entity entityA, Entity entityB) {
		float az = locMapper.get(entityA).position.z;
		float bz = locMapper.get(entityB).position.z;
		if(az > bz) {
			return 1;
		} else if(az < bz){
			return -1;
		}
		return 0;
	}
	
}
