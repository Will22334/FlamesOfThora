package com.thora.core.input;

import java.util.HashMap;

public class InputHandler {
	
	Mouse mouse = new Mouse();
	
	private HashMap<Integer, Key> registeredKeys = new HashMap<Integer, Key>();
	
	public void RegisterKey(Key keytoberegistered) {
		
		registeredKeys.put(keytoberegistered.getValue(), keytoberegistered);
	
	}
	
	public void RemoveKey(Key keytoremove) {
		
		while(registeredKeys.containsKey(keytoremove.getValue())) {
			
			registeredKeys.remove(keytoremove.getValue());
			break;
			
		}
	}
	
	
	public void handlePressedKeyEvent(int key) {
		
		// Check if the Key Event is a Registered Key
		while(registeredKeys.containsKey(key)) {
			
			//Registered Key Detected! Handling!
			
			//Get the Key Object from the Registered Keys List
			Key k = registeredKeys.get(key);
			
			//Set the key to Pressed
			k.isPressed(true);
			
			//Replace the Key with the new Value in the Registered Key List
			registeredKeys.replace(key, k);
			
			//Break out of the While
			break;
		}
		
	}

	public void handleReleasedKeyEvent(int keycode) {
		
		// Check if the Key Event is a Registered Key
		while(registeredKeys.containsKey(keycode)) {

			//Registered Key Detected! Handling!

			//Get the Key Object from the Registered Keys List
			Key k = registeredKeys.get(keycode);

			//Set the key to Released
			k.isReleased(false);

			//Replace the Key with the new Value in the Registered Key List
			registeredKeys.replace(keycode, k);

			//Break out of the While
			break;
		}

	}
	
	public void handleMousePositionChange(int screenX, int screenY) {
		// Update the mouses position
		
	}

	public void handleMouseClick(int screenX, int screenY) {
		// TODO Auto-generated method stub
		
	}
	
}
