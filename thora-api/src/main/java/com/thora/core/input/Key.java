package com.thora.core.input;

import com.badlogic.gdx.Input.Keys;

/**Name: Key
 *Package: Input
 *@author "William Klusmann"
 *
 * Purpose: This Key defines an object in which an integer and name is used to represent
 * a Key on the Keyboard. The Key has two status flags, Pressed or Released, in which the 
 * corresponding statuses will be updated.
 */
public class Key {

	//The name of the Key
	private final String name;
	
	//The Keys Assigned Value
	private final int value;
	
	//Current Status of the Key
	private boolean pressed = false;
	private boolean released = true;
	
	//Constructor for a Key
	public Key(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}
	
	public Key(int keysValue) {
		this(Keys.toString(keysValue), keysValue);
	}
	
	//Gets the name of the Key
	public String getName() {
		return name;
	}
	
	//Gets the integer value of the Key
	public int getValue() {
		return value;
	}

	//Gets whether the Key is pressed
	public boolean ifPressed() {
		return pressed;
	}
	
	//The Key has been Pressed
	public void isPressed(boolean pressed) {
		
		//No longer Released
		this.released = false;
		
		//Key Pressed
		this.pressed = pressed;
		
	}
	
	//Gets whether the Key has been released.
	public boolean ifReleased() {
		return released;
	}
	
	//The Key has been released.
	public void isReleased(boolean released) {
		this.pressed = false;
		this.released = released;
	}
	
	
}
