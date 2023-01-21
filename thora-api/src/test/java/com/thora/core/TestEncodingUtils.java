package com.thora.core;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
	
	private static final List<Integer> signedInts;
	
	private static final int[] signedIntsArr = new int[] {
			0, 1, -1,
			
			Byte.MIN_VALUE/2, (Byte.MAX_VALUE-1)/2,
			Byte.MIN_VALUE, Byte.MAX_VALUE,
			Byte.MIN_VALUE-1, Byte.MAX_VALUE+1,
			
			Short.MIN_VALUE/2, (Short.MAX_VALUE-1)/2,
			Short.MIN_VALUE, Short.MAX_VALUE,
			Short.MIN_VALUE-1, Short.MAX_VALUE+1,
			
			Integer.MIN_VALUE/2, (Integer.MAX_VALUE-1)/2,
			Integer.MIN_VALUE, Integer.MAX_VALUE
	};
	
	static {
		signedInts = Collections.unmodifiableList(IntStream.of(signedIntsArr).mapToObj(Integer::valueOf).collect(Collectors.toList()));
	}
	
	private ByteBuf buf;
	
	@BeforeAll
	void setUpBeforeClass() throws Exception {
		buf = Unpooled.buffer();
	}
	
	@BeforeEach
	void setUpEach() throws Exception {
		buf.clear();
	}
	
	public static final IntStream sourceSingedVarInts() {
		return IntStream.of(signedIntsArr);
	}
	
	@ParameterizedTest
	@MethodSource("sourceSingedVarInts")
	void testPosVarIntLoop(final int value) {
		
		EncodingUtils.writePosVarInt(value, buf);
		final int got = EncodingUtils.readPosVarIntLoop(buf);
		
		Assertions.assertEquals(value, got);
		
	}
	
	@ParameterizedTest
	@MethodSource("sourceSingedVarInts")
	void testPosVarIntUnwrapped(final int value) {
		
		EncodingUtils.writePosVarInt(value, buf);
		final int got = EncodingUtils.readPosVarIntUnwrapped(buf);
		
		Assertions.assertEquals(value, got);
		
	}
	
	@ParameterizedTest
	@MethodSource("sourceSingedVarInts")
	void testSignedVarIntProto(final int value) {
		
		EncodingUtils.writeSignedVarIntProto(value, buf);
		final int got = EncodingUtils.readSignedVarIntProto(buf);
		
		Assertions.assertEquals(value, got);
		
	}
	
	@ParameterizedTest
	@ValueSource(ints = {0, 1, 2, 4, 8, 16, 32, 64, 128-1, 2*128-1, 4*128-1, 8*128-1, 16*128-1, 32*128-1, 64*128-1, 128*128-1, 64*256-1, 2*(Short.MAX_VALUE)+1})
	void testUShort(final int value) {
		EncodingUtils.writeUShort(value, buf);
		final int got = EncodingUtils.readUShort(buf);
		
		Assertions.assertEquals(value, got);
	}
	
}
