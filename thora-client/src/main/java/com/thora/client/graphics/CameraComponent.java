package com.thora.client.graphics;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Camera;

public class CameraComponent implements Component {
	
	public static final ComponentMapper<CameraComponent> MAPPER = ComponentMapper.getFor(CameraComponent.class);
	
	public static final Camera get(Entity entity) {
		CameraComponent c = MAPPER.get(entity);
		if(c == null) return null;
		return c.camera;
	}
	
	public Camera camera;
	
	public CameraComponent(Camera camera) {
		super();
		this.camera = camera;
	}
	
}