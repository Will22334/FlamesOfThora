package com.thora.core.chat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Chat {
	
	private static final Logger logger = LogManager.getLogger(Chat.class);
	
	public static final Logger logger() {
		return logger;
	}
	
}
