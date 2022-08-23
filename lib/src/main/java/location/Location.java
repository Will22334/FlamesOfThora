package location;

public class Location {

	private static int x;
	private static int y;
	
	public Location(int x, int y) {
		
		Location.x = x;
		Location.y = y;
		
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public Location getLocation() {
		
		return this;
	}
	
}
