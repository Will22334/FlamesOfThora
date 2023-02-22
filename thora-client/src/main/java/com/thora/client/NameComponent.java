package com.thora.client;

import com.badlogic.ashley.core.Component;
import com.badlogic.ashley.core.ComponentMapper;

public class NameComponent implements Component {
	
	public static final ComponentMapper<NameComponent> MAPPER = ComponentMapper.getFor(NameComponent.class);
	
	private String name;
	
	public NameComponent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
