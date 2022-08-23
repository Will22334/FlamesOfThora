package com.thora.core.ui;

import com.thora.core.world.Location;

public abstract class UIObject {
	
	private Location position;
	
	private String name;
	
	private UIType type;

	public Location getPosition() {
		return position;
	}

	public void setPosition(Location position) {
		this.position = position;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UIType getType() {
		return type;
	}

	public void setType(UIType type) {
		this.type = type;
	} 

}
