package com.thora.core.graphics;

import java.util.Comparator;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;

public class ZComparator implements Comparator<Entity> {
	
	private static final ComponentMapper<TransformComponent> cmTrans = ComponentMapper.getFor(TransformComponent.class);
	
	@Override
	public int compare(Entity entityA, Entity entityB) {
		float az = cmTrans.get(entityA).position.z;
		float bz = cmTrans.get(entityB).position.z;
		if(az > bz) {
			return 1;
		} else if(az < bz){
			return -1;
		}
		return 0;
	}
	
}
