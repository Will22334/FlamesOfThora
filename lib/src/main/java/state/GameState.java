package state;

public abstract class GameState extends State {
	
	public GameState(String name, int id) {
		super(name, id);
		
	}
	
	public boolean finished;

	public abstract void onRender();
	
	public abstract void onPause();
	
	public abstract void onResume();
	
	public abstract void onResize();
	
	public abstract void onCreate();
	
	public abstract int getID();
	
	public abstract void setName(String name);
	
	public abstract String getName();



}
