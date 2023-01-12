package com.thora.client;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;


//Launches the Game
public class GameLauncher {
	
	private static final Logger logger = LogManager.getLogger("Launcher");
	
	private static Application app;
	
	static final Logger logger() {
		return logger;
	}
	
	static Application app() {
		return app;
	}
	
	//Main Method
	public static void main(String[] args) throws Throwable {
		
		logger().trace("Main started");
		
		
		// A basic configuration class for the Application
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		
		//Adjust configuration parameters
		config = configureApplicationSettings(config);
		
		
		//Create core app listener
		FlamesOfThoraClient game = new FlamesOfThoraClient();
		
		//A new Application.
		app = new LwjglApplication(game, config);
		
		logger().trace("Main ended");
		
	}
	
	//Reconfigures the Configuration Class
	private static LwjglApplicationConfiguration configureApplicationSettings(final LwjglApplicationConfiguration config) {
		
		config.title = "Flames of Thora";
		config.width = 800;
		config.height = 600;
		config.foregroundFPS = 60;
		config.backgroundFPS = 60;
		config.resizable = true;
		
		return config;
	}
}
