package com.thora.core;

import org.apache.logging.log4j.Logger;

public interface Console {
	
	public static Logger globalLogger() {
		return FlamesOfThora.logger;
	}
	
	public default Logger logger() {
		return FlamesOfThora.logger;
	}
	
	public default void log(String string) {
		logger().info(string);
	}
	
}
