package com.thora.core.ui;

//A basic 2d location object for UI objects

public class UIPosition {
	
	private int x;
	private int y;
	
	
	public UIPosition(int x, int y) {
		
		this.setX(x);
		this.setY(y);
		
	}

	public int getY() {
		return y;
	}


	public void setY(int y) {
		this.y = y;
	}


	public int getX() {
		return x;
	}


	public void setX(int x) {
		this.x = x;
	}

}
