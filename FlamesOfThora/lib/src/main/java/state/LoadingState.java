package state;

public class LoadingState extends GameState {

	public LoadingState(String name, int id) {
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

	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		System.out.println("Created Loading State!!");
		
	}

	@Override
	public int getID() {
		// TODO Auto-generated method stub
		return this.getId();
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getName() {
		
		return this.getStateName();
		
	}

}
