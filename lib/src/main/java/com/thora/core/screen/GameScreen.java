package com.thora.core.screen;

import com.badlogic.gdx.Screen;
import com.thora.core.GameClient;

public interface GameScreen extends Screen {
	
	
	
	GameClient getClient();
	
	String getName();
	
}
