package com.thora.core.world;

public interface IWorldEntity extends ILocatable {
	
	public static final int EMPTY_ID = -1;
	
	public int getID();
	
	public IWorldEntity setID(int id);
	
	public default boolean isRegistered(World world) {
		return isRegistered() && world.equals(getWorld());
	}
	
	public default boolean isRegistered() {
		return getID() == EMPTY_ID;
	}
	
}
