package com.thora.core.input;

import java.util.HashMap;

import com.badlogic.gdx.Input;
import com.thora.core.Console;

public class InputHandler implements Console {
	
	public static abstract class KeyBinding {
		public boolean onPress(KeyRecord key) {
			return false;
		}
		public boolean onRelease(KeyRecord key) {
			return false;
		}
		public boolean onHold(KeyRecord key) {
			return false;
		}
		public int getThrottle() {
			return -1;
		}
		public final boolean hasThrottle() {
			return getThrottle() >= 0;
		}
	}
	
	public static abstract class ThrottleKeyBinding extends KeyBinding {
		private int throttle;
		public int getThrottle() {
			return throttle;
		}
		public ThrottleKeyBinding(int throttle) {
			this.throttle = throttle;
		}
	}
	
	public static final KeyBinding UN_BOUND = new KeyBinding() {
		@Override
		public final boolean onPress(KeyRecord key) {
			return false;
		}
		@Override
		public final boolean onRelease(KeyRecord key) {
			return false;
		}
		@Override
		public final String toString() {
			return "UN-BOUND";
		}
	};
	
	public static class KeyRecord {
		public final int keycode;
		private long lastChange;
		private boolean down = false;
		
		private KeyBinding binding;
		
		KeyRecord(int keycode, KeyBinding binding) {
			this.keycode = keycode;
			this.binding = binding;
		}
		
		KeyRecord(int keycode) {
			this(keycode, null);
		}
		
		public final boolean isDown() {
			return down;
		}
		
		public final boolean isUp() {
			return !down;
		}
		
		public final long getLastChange() {
			return lastChange;
		}
		
		private KeyBinding bind(KeyBinding newBinding) {
			KeyBinding b = binding;
			binding = newBinding;
			return b;
		}
		
		private KeyBinding unbind() {
			return bind(UN_BOUND);
		}
		
		private boolean recordState(boolean state) {
			if(state != down) {
				lastChange = System.currentTimeMillis();
				down = state;
				if(isBound()) {
					if(down) {
						//press
						binding.onPress(this);
					} else {
						//release
						binding.onRelease(this);
					}
				}
			}
			return isBound();
		}
		
		public final boolean isBound() {
			return binding != null;
		}
		
		@Override
		public String toString() {
			return Input.Keys.toString(keycode) + "[" + keycode + "]";
		}
		
	}
	
	Mouse mouse = new Mouse();
	
	private KeyRecord[] keyRecords;
	
	private HashMap<Integer, Key> registeredKeys = new HashMap<Integer, Key>();
	
	
	
	public InputHandler() {
		super();
		keyRecords = new KeyRecord[256];
		for(int i=0; i<keyRecords.length; ++i) {
			keyRecords[i] = new KeyRecord(i);
		}
	}
	
	private KeyRecord getKeyRecord(int keycode) {
		return keyRecords[keycode];
	}
	
	public KeyBinding bindKey(KeyBinding binding, int keycode) {
		KeyRecord k = keyRecords[keycode];
		KeyBinding b = k.binding;
		k.binding = binding;
		return b;
	}
	
	public void RegisterKey(Key keytoberegistered) {
		
		registeredKeys.put(keytoberegistered.getValue(), keytoberegistered);
		
	}
	
	public KeyBinding unbindKey(int keycode) {
		return bindKey(UN_BOUND, keycode);
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
		return getKeyRecord(keycode).isDown();
	}
	
	public boolean recordKey(int keycode, boolean state) {
		KeyRecord k = keyRecords[keycode];
		return k.recordState(state);
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
		
		boolean result = recordKey(keycode, true);
		Key k = recordKeyPress(keycode);
		return result;
		//return k != null;
		
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
		
		boolean result = recordKey(keycode, false);
		Key k = recordKeyRelease(keycode);
		return result;
		//return k != null;
		
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
