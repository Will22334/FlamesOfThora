package com.thora.core.world;

import java.util.Objects;

public interface WorldEntity extends Locatable {
	
	public static final int EMPTY_ID = -1;
	
	public int getID();
	
	public WorldEntity setID(int id);
	
	public default boolean isRegistered(World world) {
		return isRegistered() && Objects.equals(getWorld(), world);
	}
	
	public default boolean isRegistered() {
		return getID() == EMPTY_ID;
	}
	
}
