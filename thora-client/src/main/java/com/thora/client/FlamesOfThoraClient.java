package com.thora.client;


import java.awt.Dimension;
import java.net.InetSocketAddress;
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
import com.thora.client.state.GameState;
import com.thora.client.state.LoadingState;
import com.thora.client.state.MenuState;
import com.thora.client.state.PlayingState;
import com.thora.client.state.StateManager;
import com.thora.core.HasLogger;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.net.netty.NettyNetworkManager;
import com.thora.core.world.HashChunkWorld;
import com.thora.core.world.Pole;
import com.thora.core.world.TileGenerator;
import com.thora.core.world.AbstractWorld;
import com.thora.core.world.generator.PerlinTileGenerator;

public class FlamesOfThoraClient implements ApplicationListener, HasLogger {
	
	public static final int IO_WORKER_THREADS = 1;
	
	//TODO Compute view distance on start/resize
	public static final int DEFAULT_VIEW_RANGE = 15;
	
	public static final float DEFAULT_WORLD_SCALE = 35f;
	public static final float DEFAULT_WORLD_FREQ = 20f;
	
	private final static int LOADINGSTATEID = 0;
	private final static int MENUSTATEID = 1;
	private final static int PLAYINGSTATEID = 2;
	
	public static final Logger logger = LogManager.getLogger(FlamesOfThoraClient.class.getPackage().getName() + ".Client");
	
	private PublicKey serverIdentity = null;
	private Cipher publicEncCipher = null;
	
	private PooledEngine engine = new PooledEngine();
	
	//Manages the various states and provides switching between them
	public StateManager States = new StateManager();
	
	public InetSocketAddress serverAddress;
	
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
			this.publicEncCipher = EncodingUtils.generateCipher(pub);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		//Add the states to the StateManager for indexing and management.
		initializeStates();
		
		//Runs the create command for all states.
		States.onCreate();
		
		TileGenerator gen = new PerlinTileGenerator((int)System.currentTimeMillis(), DEFAULT_WORLD_SCALE, DEFAULT_WORLD_FREQ);
		Dimension size = new Dimension(300, 300);
		Pole origin = new Pole("Origin", 0 ,0);
		
		
		//world = new KeyMapWorld(ConcurrentHashMap::new, "Earth", size, origin, gen);
		//world = new ArrayWorld("Earth", size, origin, 30, gen);
		world = new HashChunkWorld("Earth", origin, 15, 15, null);
		
		try {
			world.initialize();
		} catch (Exception e) {
			logger().atError().withThrowable(e).log("Failed to initialize {}", world);
			throw new RuntimeException(e);
		}
		
		logger().debug("World Backend: {} {}", world.getClass().getSimpleName(), world.getEstimatedArea());
		
		this.network = new NettyNetworkManager(IO_WORKER_THREADS, serverIdentity, this.publicEncCipher);
		
		States.setActiveState(LOADINGSTATEID);
		
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
		//oldState.render(Gdx.graphics.getDeltaTime());
		
		//		while(States.isStateFinished() != false) {
		//			
		//			log("Detected change in state. :  Exited : in " + States.getActiveState().getName());
		//			States.setActiveState(States.getActiveState().getID() + 1);
		//			
		//		}
//		if(States.isStateFinished()) {
//			States.setNextState();
//		}
//		States.checkForExit();
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
	
	
}
