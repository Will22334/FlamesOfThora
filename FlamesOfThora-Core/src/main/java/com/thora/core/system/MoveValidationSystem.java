package com.thora.core.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.thora.core.IntVector;
import com.thora.core.world.MovableComponent;
import com.thora.core.world.MoveEventComponent;

/**
 * A {@link IteratingSystem} that filters {@link MoveEventComponent MoveEvents} that an entity requests.
 * Currently disallows entities that are not {@link MovableComponent MovableComponent},
 * a move farther than 1 tile(including diagonal), or is zero.
 * This system can easily include other filtering
 * conditions such as (frozen or immobilized) entities with on-(success/fail) side effects.
 * @author Dave
 *
 */
public class MoveValidationSystem extends IteratingSystem {
	
	public static final Family FAMILY = MoveSystem.FAMILY;
	private static final ComponentMapper<MoveEventComponent> moveM = ComponentMapper.getFor(MoveEventComponent.class);
	private static final ComponentMapper<MovableComponent> movableM = ComponentMapper.getFor(MovableComponent.class);
	
	public MoveValidationSystem(int priority) {
		super(FAMILY, priority);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		if(movableM.has(entity)) {
			//Validate move vector
			MoveEventComponent event = moveM.get(entity);
			IntVector v = event.v;
			if(v.maxRectLength() > 1 || v.isZero()) {
				//Rejected due to being more than 1x1 or 0x0
				entity.remove(MoveEventComponent.class);
			}
		} else {
			entity.remove(MoveEventComponent.class);
		}
	}
	
}
