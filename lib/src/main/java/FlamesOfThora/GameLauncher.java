package FlamesOfThora;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


//Launches the Game
public class GameLauncher {

	//Main Method
	public static void main(String[] args) {
		
		// A basic configuration class for the Application
	    LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
	   
	    //Adjust configuration parameters
	    config = configureApplicationSettings(config);
	    
	    //A new Application.
	    new LwjglApplication(new FlamesOfThora(), config);
	      
	}
	
	//Reconfigures the Configuration Class
	private static LwjglApplicationConfiguration configureApplicationSettings(LwjglApplicationConfiguration config) {
		
		config.title = "Flames of Thora";
		config.height = 600;
		config.width = 800;
		config.resizable = true;
		
		return config;
	}
}
