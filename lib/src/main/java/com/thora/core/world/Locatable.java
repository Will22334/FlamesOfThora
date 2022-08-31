package com.thora.core.world;

/**
 * Anything that has a {@link Location}. This interface delegates Location methods to {@link #getLocation()}
 * for re-usability and convenience.
 * @author Dave
 *
 */
public interface Locatable {
	
	public Location getLocation();
	
	public default int getX() {
		return getLocation().getX();
	}
	
	public default int getY() {
		return getLocation().getY();
	}
	
	public default double getDistance(Locatable loc) {
		return getLocation().getDistance(loc.getLocation());
	}
	
	public default boolean isInRange(Locatable loc, double range) {
		return getDistance(loc) <= range;
	}
	
}
