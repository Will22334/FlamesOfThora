package com.thora.core;

import static org.junit.jupiter.api.Assertions.fail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.thora.core.net.netty.EncodingUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;

@TestInstance(Lifecycle.PER_CLASS)
class TestEncodingUtils {
	
	private static final Logger logger = LogManager.getLogger(TestEncodingUtils.class);
	
	private static final Logger logger() {
		return logger;
	}
	
	private ByteBuf buf;
	
	@BeforeAll
	void setUpBeforeClass() throws Exception {
		buf = Unpooled.buffer();
	}
	
	@Test
	void testwritePosVarInt() {
		int expected = Short.MAX_VALUE/2+1;
		
		int totalPosBits = EncodingUtils.writePosVarIntCount(expected, buf);
		
		logger.info("\n" + ByteBufUtil.prettyHexDump(buf));
		buf.clear();
		
		int totalSignedBits = EncodingUtils.writeSignedVarIntCount(expected, buf);
		logger.info("\n" + ByteBufUtil.prettyHexDump(buf));
		
		logger().info("VAL={}, Pos bits {}, Signed bits {}", expected, totalPosBits, totalSignedBits);
		
		//fail("Not yet implemented");
	}
	
}
