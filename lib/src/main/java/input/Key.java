package input;

public class Key {

	private String name;
	private int value;
	
	private boolean pressed = false;
	private boolean released = true;
	
	public Key(String name, int value) {
		super();
		this.name = name;
		this.value = value;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public boolean ifPressed() {
		return pressed;
	}
	
	public void isPressed(boolean pressed) {
		
		//No longer Released
		this.released = false;
		
		//Key Pressed
		this.pressed = pressed;
		
	}
	
	public boolean ifReleased() {
		return released;
	}
	public void isReleased(boolean released) {
		this.pressed = false;
		this.released = released;
	}
	
	
}
