package com.thora.core;

import org.apache.logging.log4j.Logger;

public interface HasLogger {
	
	public static Logger globalLogger() {
		return FlamesOfThora.logger;
	}
	
	public default Logger logger() {
		return FlamesOfThora.logger;
	}
	
}
