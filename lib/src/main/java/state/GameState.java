package state;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import FlamesOfThora.Console;

public abstract class GameState extends State implements Console {
	
	private static final Logger logger = LogManager.getLogger("DefaultConsole");
	
	public GameState(String name, int id) {
		super(name, id);
		
	}
	
	public boolean finished;
	
	@Override
	public Logger logger() {
		return logger;
	}
	
	public abstract void onRender();
	
	public abstract void onPause();
	
	public abstract void onResume();
	
	public abstract void onResize();
	
	public abstract void onCreate();
	
	public abstract int getID();
	
	public abstract void setName(String name);
	
	public abstract String getName();



}
