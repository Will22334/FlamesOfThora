package com.thora.core.world;

import java.util.Objects;

public interface HasWorld {
	
	public static boolean sameWorld(HasWorld a, HasWorld b) {
		return Objects.equals(a.getWorld(), b.getWorld());
	}
	
	public World getWorld();
	
	/**
	 * Returns true if this Ojbect is located in the passed world
	 * @param w The world to compare
	 * @return {@code true} if this is in the world
	 */
	public default boolean contains(HasWorld w) {
		return sameWorld(this, w);
	}
	
}
