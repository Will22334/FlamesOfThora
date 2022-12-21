package com.thora.core.ui;

import com.badlogic.gdx.scenes.scene2d.Actor;

public abstract class UIObject extends Actor {
	
	private UIPosition position;
	
	private String name;
	
	private UIType type;
	
	public UIObject() {
		
		position = new UIPosition(0,0);
	}

	public UIPosition getPosition() {
		return position;
	}

	public void setPosition(UIPosition position) {
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
