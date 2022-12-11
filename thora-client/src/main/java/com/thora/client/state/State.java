package com.thora.client.state;

import java.util.Objects;

public abstract class State {
	
	private final int id;
	private final String stateName;
	
	public State(String name, int id) {
		this.stateName = name;
		this.id = id;
	}
	
	public final int getID() {
		return id;
	}
	
	public final String getStateName() {
		return stateName;
	}
	
	@Override
	public String toString() {
		return getStateName();
	}
	
	@Override
	public boolean equals(Object o) {
		if(o == null) return false;
		if(o instanceof State) {
			State s = (State) o;
			return this.id == s.id &&
					this.stateName.equals(s.stateName);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, stateName);
	}
	
}
