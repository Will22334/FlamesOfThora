package FlamesOfThora;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.ApplicationListener;

import state.LoadingState;
import state.MenuState;
import state.PlayingState;
import state.StateManager;

public class FlamesOfThora implements ApplicationListener, Console {
	
	public static final Logger logger = LogManager.getLogger("Server");
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	//Manages the various states and provides switching between them
	public StateManager States = new StateManager();
	
	private final static int LOADINGSTATEID = 0;
	private final static int MENUSTATEID = 1;
	private final static int PLAYINGSTATEID = 2;
	
	
	//Initiate the States
	public void initializeStates() {
		
		States.addStateToList(new MenuState("Menu State", MENUSTATEID));
		States.addStateToList(new PlayingState("Playing State", PLAYINGSTATEID));
		States.addStateToList(new LoadingState("Loading State", LOADINGSTATEID));
		
	}
	
	//Create the 
	@Override
	public void create() {
		
		//Add the states to the StateManager for indexing and management.
		initializeStates();
		
		//Runs the create command for all states.
		States.onCreate();
		
		States.setActiveState(LOADINGSTATEID);
		
	}

	//Called whenever the Application is resized.
	@Override
	public void resize(int width, int height) {
		// Resize the Application
		
		States.getActiveState().onResize();
		
	}

	@Override
	public void render() {
		// Render the game based on the current state
		
		States.getActiveState().onRender();
		
		while(States.getActiveState().finished != false) {
			
			log("Detected change in state. :  Exited : in " + States.getActiveState().getName());
			States.setActiveState(States.getActiveState().getID() + 1);	}
	}

	@Override
	public void pause() {
		
		States.getActiveState().onPause();

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}
	

	public static int getLoadingstate() {
		return LOADINGSTATEID;
	}

	public static int getMenustateid() {
		return MENUSTATEID;
	}

	public static int getPlayingstateid() {
		return PLAYINGSTATEID;
	}
	

}
