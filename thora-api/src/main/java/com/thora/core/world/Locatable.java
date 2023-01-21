package com.thora.core.world;

import com.badlogic.ashley.core.Entity;

/**
 * Anything that has a {@link Location}. This interface delegates Location methods to {@link #getLocation()}
 * for re-usability and convenience.
 * @author Dave
 *
 */
public interface Locatable extends HasWorld {
	
	public static Locatable getComp(Entity e) {
		return LocationComponent.MAPPER.get(e);
	}
	
	public Location getLocation();
	
	@Override
	public default World world() {
		return getLocation().world();
	}
	
	public default Tile getTile() {
		return world().getTile(getLocation());
	}
	
	public default int getX() {
		return getLocation().getX();
	}
	
	public default int getY() {
		return getLocation().getY();
	}
	
	public default int getOrthogonallDistance(Locatable loc) {
		return getLocation().getOrthogonallDistance(loc.getLocation());
	}
	
	public default double getDistance(Locatable loc) {
		return getLocation().getDistance(loc.getLocation());
	}
	
	public default boolean isInRange(Locatable loc, double range) {
		return getDistance(loc) <= range;
	}
	
	public default boolean isInWalkingRange(Locatable loc, double range) {
		return getLocation().isInWalkingRange(loc, range);
	}
	
}
