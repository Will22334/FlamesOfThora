package com.thora.core.world;

public interface Region {
	
	/**
	 * Returns {@code true} if the given {@link Locatable} is inside this Region.
	 * @param loc the location in question
	 * @return {@code true} if the Location is inside this {@link Region},
	 * {@code false} if the Location is not in this Region.
	 */
	public boolean contains(Locatable loc);
	
}
