package com.thora.client.system;

import com.badlogic.ashley.core.ComponentMapper;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.thora.client.FlamesOfThoraClient;
import com.thora.core.IntVector;
import com.thora.core.world.MovableComponent;
import com.thora.core.world.MoveRequestComponent;

/**
 * A {@link IteratingSystem} that filters {@link MoveRequestComponent MoveEvents} that an entity requests.
 * Currently disallows entities that are not {@link MovableComponent MovableComponent},
 * a move farther than 1 tile(including diagonal), or is zero.
 * This system can easily include other filtering
 * conditions such as (frozen or immobilized) entities with on-(success/fail) side effects.
 * @author Dave
 *
 */
public class MoveValidationSystem extends IteratingSystem {
	
	public static final Family FAMILY = MoveSystem.FAMILY;
	private static final ComponentMapper<MoveRequestComponent> moveM = ComponentMapper.getFor(MoveRequestComponent.class);
	private static final ComponentMapper<MovableComponent> movableM = ComponentMapper.getFor(MovableComponent.class);
	
	public MoveValidationSystem(int priority) {
		super(FAMILY, priority);
	}
	
	protected boolean canMove(Entity entity) {
		return movableM.has(entity);
	}
	
	protected boolean isValid(IntVector v) {
		return v.maxRectLength() == 1;
	}
	
	protected MoveRequestComponent cancelMove(Entity entity) {
		return entity.remove(MoveRequestComponent.class);
	}
	
	@Override
	protected void processEntity(Entity entity, float deltaTime) {
		
		if(!(canMove(entity) &&
				isValid(moveM.get(entity).v))) {
			FlamesOfThoraClient.logger.info("Cancel move {}", moveM.get(entity));
			cancelMove(entity);
		}
		
//		if(movableM.has(entity)) {
//			//Validate move vector
//			MoveEventComponent event = moveM.get(entity);
//			IntVector v = event.v;
//			if(v.maxRectLength() > 1 || v.isZero()) {
//				//Rejected due to being more than 1x1 or 0x0
//				cancelMove(entity);
//			}
//		} else {
//			cancelMove(entity);
//		}
	}
	
}
