package input;

import location.Location;

public class Mouse {
	
	Location loc;
	
	public void setLocation(int x, int y) {
		
		loc = new Location(x,y);
		
	}
	
	public Location getMouseLocation() {
		
		return loc;
		
	}

}
