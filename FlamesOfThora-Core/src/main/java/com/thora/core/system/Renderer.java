package com.thora.core.system;

public abstract class Renderer {
	
	private int updateInterval = 1;
	private int lastUpdate;
	
	
	public abstract void update(float deltaTime);


	public int getUpdateInterval() {
		return updateInterval;
	}


	public void setUpdateInterval(int updateInterval) {
		this.updateInterval = updateInterval;
	}


	public int getLastUpdate() {
		return lastUpdate;
	}


	public void setLastUpdate(int lastUpdate) {
		this.lastUpdate = lastUpdate;
	}	

	
	
}
