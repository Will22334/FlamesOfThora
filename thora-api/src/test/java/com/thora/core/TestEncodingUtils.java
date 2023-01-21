package com.thora.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.google.common.base.Objects;
import com.thora.core.math.BasicDoubleVector;
import com.thora.core.math.Vector;
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
	
	@AfterEach
	void checkBuffer() throws Exception {
		if(buf.readableBytes() > 0) {
			
		}
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
	
	private static class NObj {
		public String name;
		public Vector vector;
		@Override
		public String toString() {
			return "[" + name + ": " + vector + "]";
		}
		@Override
		public int hashCode() {
			return Objects.hashCode(name, vector);
		}
		@Override
		public boolean equals(Object obj) {
			if(obj == null) return false;
			if(this == obj) return true;
			if(obj instanceof NObj) {
				NObj o = (NObj) obj;
				return Objects.equal(name, o.name) && Objects.equal(vector, o.vector);
			}
			return false;
		}
		
	}
	
	private static void encodeNObj(final NObj o, final ByteBuf buf) {
		EncodingUtils.writeString(o.name, buf);
		encodeVector(o.vector, buf);
	}
	
	private static void encodeVector(final Vector v, final ByteBuf buf) {
		buf.writeDouble(v.getX());
		buf.writeDouble(v.getY());
	}
	
	private static NObj decodeNObj(final ByteBuf buf) {
		NObj o = new NObj();
		o.name = EncodingUtils.readString(buf);
		o.vector = decodeVector(buf);
		return o;
	}
	
	private static Vector decodeVector(final ByteBuf buf) {
		final double x = buf.readDouble();
		final double y = buf.readDouble();
		return new BasicDoubleVector(x, y);
	}
	
	@Test
	void testEncodeCollection() {
		try {
			final Collection<NObj> c = new LinkedList<>();
			final int size = 20;
			for(int i=0; i<size; ++i) {
				NObj o = new NObj();
				o.name = "O-" + (i + 1);
				o.vector = new BasicDoubleVector(i * 1.25d, i * -3.75d);
				c.add(o);
			}
			
			EncodingUtils.encodeSizedCollection(c, TestEncodingUtils::encodeNObj, buf);
			
			final Collection<NObj> readList = EncodingUtils.decodeSizedCollection(ArrayList::new, TestEncodingUtils::decodeNObj, buf);
			
			Assertions.assertEquals(0, buf.readableBytes(), "There are left over bytes after decoding Collection!");
			Assertions.assertEquals(c, readList);
			
		} catch(Throwable t) {
			logger().atWarn().withThrowable(t).log("Encode/Decode Collection failed!\n{}", () -> ByteBufUtil.prettyHexDump(buf));
			throw t;
		}
	}
	
}
