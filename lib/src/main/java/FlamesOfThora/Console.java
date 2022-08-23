package FlamesOfThora;

import org.apache.logging.log4j.Logger;

public interface Console {
	
	public default Logger logger() {
		return FlamesOfThora.logger;
	}
	
	public default void log(String s) {
		logger().info(s);
	}
	
}
