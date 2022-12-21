package com.thora.core.ui;

import com.badlogic.gdx.Input.TextInputListener;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;

public class UITextBox extends UIObject implements TextInputListener, Initializable  {
	
	//The name
	private String entry;
	
	//Whether or not the TextBox is selected
	boolean boxSelected = false;

	
	TextField entryField;
 
	//Location of the Text Box
	UIPosition textboxPosition;
	
	//The color of the textbox background
	Color backgroundColor;
	
	//Constructor
	public UITextBox(String name, UIPosition textboxposition) {
		
		this.textboxPosition = textboxposition;
		this.setEntry(name);
		
	}
	
	@Override
	public void input(String text) {
		// TODO Auto-generated method stub
		boxSelected = true;
		
	}

	@Override
	public void canceled() {
		// TODO Auto-generated method stub
		boxSelected = false;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void draw(Batch batch, float parentAlpha) {
		// TODO Auto-generated method stub
		
		super.draw(batch, parentAlpha);
		backgroundColor = Color.WHITE;
	}

	public String getEntry() {
		return entry;
	}

	public void setEntry(String entry) {
		this.entry = entry;
	}
	
}
