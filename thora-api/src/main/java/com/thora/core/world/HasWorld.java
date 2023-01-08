package com.thora.core.world;

import java.util.Objects;

public interface HasWorld {
	
	public static boolean sameWorld(HasWorld a, HasWorld b) {
		return Objects.equals(a.getWorld(), b.getWorld());
	}
	
	public World getWorld();
	
	public default boolean sameWorld(HasWorld a) {
		return sameWorld(this, a);
	}
	
}
