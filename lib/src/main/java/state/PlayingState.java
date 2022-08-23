package state;

<<<<<<< HEAD
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
=======
import input.InputHandler;
import input.InputListener;
>>>>>>> branch 'master' of https://github.com/Will22334/FlamesOfThora.git

public class PlayingState extends GameState {
	
	private static final Logger logger =  LogManager.getLogger(LoadingState.class);
	
	public final Logger logger() {
		return logger;
	}
	
	public PlayingState(String name, int id) {
		super(name, id);
		
		
	}
	
<<<<<<< HEAD
=======
	InputHandler inputHandler = new InputHandler();
	InputListener inputListener = new InputListener(inputHandler);

>>>>>>> branch 'master' of https://github.com/Will22334/FlamesOfThora.git
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		logger().info("Created Playing State!!");
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
		// TODO Auto-generated method stub
		
	}
	
}
