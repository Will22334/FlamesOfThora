package com.thora.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ThoraGameEngine extends GameEngine {
	
	public ThoraGameEngine(int sideThreads, Logger logger) {
		super(sideThreads, logger);
	}
	
	public ThoraGameEngine(int sideThreads) {
		super(sideThreads, LogManager.getLogger(GameEngine.class));
	}
	
}
