package com.thora.core.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool.Poolable;
import com.thora.core.IntVector;

public class MoveEventComponent implements Component, Poolable {
	
	public static final Family FAMILY = Family.one(MoveEventComponent.class).get();
	public static final ComponentMapper<MoveEventComponent> MAPPER = ComponentMapper.getFor(MoveEventComponent.class);
	
	public IntVector v = new IntVector();
	
	public Location getEnd(Locatable loc) {
		return loc.getLocation().shift(v.x, v.y);
	}
	
	public MoveEventComponent set(Vector2 b) {
		v.x += b.x;
		v.y += b.y;
		return this;
	}
	
	@Override
	public void reset() {
		v.clear();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + v;
	}
	
}
