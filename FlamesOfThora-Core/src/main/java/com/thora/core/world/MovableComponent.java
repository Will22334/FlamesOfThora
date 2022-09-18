package com.thora.core.world;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.signals.Signal;

/**
 * A component that indicates an entity can moved.
 * This class is solely used as a tag component.
 * @author Dave
 *
 */
public class MovableComponent implements Component {
	
	public static final ComponentMapper<MovableComponent> MAPPER = ComponentMapper.getFor(MovableComponent.class);
	
	public static final MovableComponent create() {
		return new MovableComponent();
	}
	
	public final Signal<MoveEvent> signal = new Signal<>();
	
	public MovableComponent() {
		
	}
	
	public final void dispatch(MoveEvent event) {
		signal.dispatch(event);
	}
	
}
