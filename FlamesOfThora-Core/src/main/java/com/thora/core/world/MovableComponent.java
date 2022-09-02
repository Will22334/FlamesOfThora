package com.thora.core.world;

import com.badlogic.ashley.core.Component;

/**
 * A component that indicates an entity can moved.
 * This class is solely used as a tag component.
 * @author Dave
 *
 */
public class MovableComponent implements Component {
	
	public static final MovableComponent INSTANCE = new MovableComponent();
	
	private MovableComponent() {
		
	}
	
}
