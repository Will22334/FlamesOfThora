package com.thora.core.input;

import java.util.HashMap;

import com.thora.core.Console;

public class InputHandler implements Console {
	
	Mouse mouse = new Mouse();
	
	private HashMap<Integer, Key> registeredKeys = new HashMap<Integer, Key>();
	
	public void RegisterKey(Key keytoberegistered) {
		
		registeredKeys.put(keytoberegistered.getValue(), keytoberegistered);
		
	}
	
	public void RemoveKey(Key keytoremove) {
		
		registeredKeys.remove(keytoremove.getValue());
		
	}
	
	public boolean isKeyDown(int... keys) {
		for(int code: keys) {
			if(!isKeyDown(code)) return false;
		}
		return true;
	}
	
	public boolean isKeyDown(int keycode) {
		Key k = registeredKeys.get(keycode);
		if(k == null) return false;
		return k.ifPressed();
	}
	
	Key recordKeyPress(int keycode) {
		//Get key record associated with the code
		Key k = registeredKeys.get(keycode);
		
		//
		if(k != null) {
			k.isPressed(true);
		}
		
		return k;
	}
	
	public boolean handlePressedKeyEvent(int keycode) {
		
		Key k = recordKeyPress(keycode);
		
		return k != null;
		
		// Check if the Key Event is a Registered Key
		//		while(registeredKeys.containsKey(key)) {
		//			
		//			//Registered Key Detected! Handling!
		//			
		//			//Get the Key Object from the Registered Keys List
		//			Key k = registeredKeys.get(key);
		//			
		//			//Set the key to Released
		//			k.isPressed(true);
		//			
		//			//Replace the Key with the new Value in the Registered Key List
		//			registeredKeys.replace(key, k);
		//			
		//			//Break out of the While
		//			break;
		//		}
		
	}
	
	Key recordKeyRelease(int keycode) {
		//Get key record associated with the code
		Key k = registeredKeys.get(keycode);
		
		//
		if(k != null) {
			k.isReleased(false);
		}
		
		return k;
	}
	
	public boolean handleReleasedKeyEvent(int keycode) {
		
		Key k = recordKeyRelease(keycode);
		
		return k != null;
		
		// Check if the Key Event is a Registered Key
		//		while(registeredKeys.containsKey(keycode)) {
		//			
		//			//Registered Key Detected! Handling!
		//			
		//			//Get the Key Object from the Registered Keys List
		//			Key k = registeredKeys.get(keycode);
		//			
		//			//Set the key to Released
		//			k.isReleased(false);
		//			
		//			//Replace the Key with the new Value in the Registered Key List
		//			registeredKeys.replace(keycode, k);
		//			
		//			//Break out of the While
		//			break;
		//		}
		
	}
	
	public void handleMousePositionChange(int screenX, int screenY) {
		
	}
	
	public void handleMouseClick(int screenX, int screenY) {
		
	}
	
	public boolean handlScrolled(int amount) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
