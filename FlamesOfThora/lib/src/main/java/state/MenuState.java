package state;

public class MenuState extends GameState {

	public MenuState(String name, int id) {
		super(name, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onRender() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResize() {
		// TODO Auto-generated method stub
		System.out.println("Resized!");
	}

	@Override
	public void onCreate() {
		// 
		System.out.println("Created Menu State!");
		
	}

	@Override
	public int getID() {
		//Returns the ID of the State;
		return this.getId();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.getStateName();
	}

}
