package com.thora.core.net;

import java.net.InetSocketAddress;
import java.util.Map;

public interface ServerNetwork {
	
	Map<InetSocketAddress,? extends NetworkChannel> boundChannels();
	
	
	
}
