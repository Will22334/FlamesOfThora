package com.thora.core;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.NettyNetworkManager;
import com.thora.core.state.GameState;
import com.thora.core.state.LoadingState;
import com.thora.core.state.LoginState;
import com.thora.core.state.MenuState;
import com.thora.core.state.PlayingState;
import com.thora.core.state.StateManager;
import com.thora.core.world.AbstractWorld;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Pole;

public class FlamesOfThora implements ApplicationListener, HasLogger {
	
	public static final String defaultAddress = "localhost:8080";
	
	public static final int IO_WORKER_THREADS = 1;
	
	//TODO Compute view distance on start/resize
	public static final int DEFAULT_VIEW_RANGE = 15;
	
	public static final float DEFAULT_WORLD_SCALE = 35f;
	public static final float DEFAULT_WORLD_FREQ = 20f;
	
	private final static int LOADINGSTATEID = 0;
	private final static int LOGINSTATEID = 1;
	private final static int MENUSTATEID = 2;
	private final static int PLAYINGSTATEID = 3;
	
	public static final Logger logger = LogManager.getLogger("Client");
	
	private PublicKey serverIdentity = null;
	private Cipher publicEncCipher = null;
	
	private PooledEngine engine = new PooledEngine();
	
	//Manages the various states and provides switching between them
	public StateManager States = new StateManager();
	
	public static PublicKey getServerKey() {
		return serverKey;
	}
	
	private static PublicKey serverKey;
	
	private NettyNetworkManager network;
	private AbstractWorld world;
	
	@Override
	public final Logger logger() {
		return logger;
	}
	
	public PooledEngine engine() {
		return engine;
	}
	
	public NettyNetworkManager network() {
		return network;
	}
	
	public AbstractWorld world() {
		return world;
	}
	
	//Initiate the States
	public void initializeStates() {
		States.addStateToList(new MenuState(this, "Menu State", MENUSTATEID));
		States.addStateToList(new LoginState(this, "Login State", LOGINSTATEID));
		States.addStateToList(new PlayingState(this, "Playing State", PLAYINGSTATEID));
		States.addStateToList(new LoadingState(this, "Loading State", LOADINGSTATEID));
		
	}
	
	protected GameState activeState() {
		return States.getActiveState();
	}
	
	//Create the 
	@Override
	public void create() {
		
		Path dir = Paths.get("./keys/");
		PublicKey pub = null;
		try {
			pub = readPublicKey(dir.resolve("publicKey"));
			this.serverIdentity = pub;
			serverKey = pub;
			this.publicEncCipher = EncodingUtils.generateCipher(pub);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		//Add the states to the StateManager for indexing and management.
		initializeStates();
		
		//Initialize all states.
		States.initialize();
		
		//Define an initial location to base the world upon
		Pole origin = new Pole("Origin",0,0);
		
		//Create a new World object at the location and size defined
		world = new HashChunkWorld("Earth", origin, 15, 15, new PooledEngine(), null);
		
		//Attempt to create the world
		try {
			world.initialize();
			
		} catch (Exception e) {
			logger().atError().withThrowable(e).log("Failed to initialize {}", world);
			throw new RuntimeException(e);
		}
		
		logger().debug("World Backend: {} {}", world.getClass().getSimpleName(), world.getEstimatedArea());
		
		//Creates a new network object.
		this.network = new NettyNetworkManager(IO_WORKER_THREADS, serverIdentity, this.publicEncCipher);
		
		//Sets the active state to the Loading State. 
		States.setActiveState(LOGINSTATEID);
		
	}
	
	protected static PublicKey readPublicKey(Path path) throws Exception {
		
		byte[] keyBytes = Files.readAllBytes(path);
		
		X509EncodedKeySpec spec =
				new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey key = kf.generatePublic(spec);
		System.out.println("Read " + key.hashCode() + " from " + path);
		return key;
		
	}
	
	//Called whenever the Application is resized.
	@Override
	public void resize(int width, int height) {
		// Resize the Application
		
		activeState().onResize(width, height);
		
	}
	
	@Override
	public void render() {
		
		GameState oldState = activeState();
		
		// Render the game based on the current state
		oldState.updateAndRender();
		oldState.render(Gdx.graphics.getDeltaTime());
		
				while(States.isStateFinished() != false) {
					
					logger.debug("Detected change in state. :  Exited : in " + States.getActiveState().getName());
					States.setActiveState(States.getActiveState().getID() + 1);
					
				}
		if(States.isStateFinished()) {
			States.setNextState();
		}
		States.checkForExit();
	}
	
	@Override
	public void pause() {
		
		activeState().onPause();
		
	}
	
	@Override
	public void resume() {
		
		activeState().onResume();
		
	}
	
	@Override
	public void dispose() {
		logger().trace("DISPOSING:\t{}", this);
	}
	
	@Override
	public String toString() {
		return activeState().toString();
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
	
	public static int getLoginstateid() {
		return LOGINSTATEID;
	}
 	
	
}
