package com.thora.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.thora.core.net.netty.EncodingUtils;


/**
 * Old thora-api GameLauncher.
 * @author Dave
 *
 */
@Deprecated
public class GameLauncher {

	private static final Logger logger = LogManager.getLogger("Launcher");

	static final Logger logger() {
		return logger;
	}

	//Main Method
	public static void main(String[] args) throws Throwable {

		logger().trace("Main started");

		Path dir = Paths.get("./assets/keys/publicKey");

		PublicKey keyIdentity;
		try {
			keyIdentity = EncodingUtils.readPublicKey(dir);
		} catch (Throwable t) {
			logger().atLevel(Level.ERROR).withThrowable(t).log("Failed to read server identity!");
			throw t;
		}

		logger().trace("Server Public Key = {}", keyIdentity);


		// A basic configuration class for the Application
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();

		//Adjust configuration parameters
		config = configureApplicationSettings(config);


		//Create core app listener
		FlamesOfThora game = new FlamesOfThora();


		//A new Application.
		Application app = new LwjglApplication(game, config);


		logger().trace("Main ended");

	}

	//Reconfigures the Configuration Class
	private static LwjglApplicationConfiguration configureApplicationSettings(LwjglApplicationConfiguration config) {

		config.title = "Flames of Thora";
		config.width = 800;
		config.height = 600;
		config.foregroundFPS = -1;
		config.resizable = true;

		return config;
	}
}
