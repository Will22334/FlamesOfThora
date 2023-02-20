package com.thora.core;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Assertions;

import com.thora.core.net.netty.EncodingUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class VarIntBenchmark {
	
	private static final Logger logger = LogManager.getLogger(VarIntBenchmark.class);
	
	private static final int seed = 90353453;
	
	private static final Random rand = new Random(seed);
	private static final int totalCalls = 100_000_000;
	private static final int[] values;
	
	static {
		values = new int[totalCalls];
		for(int i=0; i<totalCalls; ++i) {
			//values[i] = Math.abs(rand.nextInt());
			
			
			//values[i] = -1 * Math.abs(rand.nextInt());
			
			values[i] = (int) (-1 * Math.abs(rand.nextInt() / 4d));
			
			
			
			//values[i] = rand.nextInt();
			
			//values[i] = (int) (rand.nextInt() / 1.25f);
			//values[i] = (int) (rand.nextInt() / 3);
			
			
			
			//values[i] = rand.nextInt() / 10;
		}
	}
	
	private static final Logger logger() {
		return logger;
	}
	
	private static final int randSigned() {
		return rand.nextInt();
	}
	
	private static final int randUnsigned() {
		return Math.abs(randSigned());
	}
	
	public static void main(String[] args) {
		
		final ByteBuf buf = Unpooled.buffer(633_701_490);
		
		Utils.Timer t = new Utils.Timer();
		long time;
		
		unsignedVarIntProto(totalCalls, buf);
		buf.clear();
		
		
		
		
//		t = new Utils.Timer();
//		t.start();
//		signedVarIntUnwrapped(totalCalls, buf);
//		time = t.mark();
//		logger().info("{}ms\n", time);
		
		
		
		
		
		t.start();
		unSignedVarIntProtoLoop(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tUnsignedVarIntProtoLoop\n", time);
		buf.clear();
		t.nextMark();
		
		unsignedVarIntProto(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tUnsignedVarIntProto\n", time);
		buf.clear();
		t.mark();
		
		posVarInt(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tPosVarInt\n", time);
		buf.clear();
		t.mark();
		
		
		posVarIntUnwrapped(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tPosVarIntUnwrapped\n", time);
		buf.clear();
		t.mark();
		
		
		
		signedVarIntLoop(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tSignedVarIntLoop\n", time);
		buf.clear();
		t.mark();
		
		
		signedVarIntProto(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tSignedVarIntProto\n", time);
		buf.clear();
		t.mark();
		
		
		signedVarIntProtoLoop(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tSignedVarIntProtoLoop\n", time);
		buf.clear();
		t.mark();
		
		
		
		signedVarIntUnwrapped(totalCalls, buf);
		time = t.nextMark();
		logger().info("{}ms\t\tSignedVarIntUnwrapped\n", time);
		buf.release();
		
		
	}
	
	private static void posVarInt(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writePosVarInt(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readPosVarInt(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("PosVarInt wrote " + bytes);
		buf.clear();
	}
	
	private static void posVarIntUnwrapped(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writePosVarIntUnwrapped(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readPosVarIntUnwrapped(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("PosVarIntUnwrapped wrote " + bytes);
		buf.clear();
	}
	
	private static void unSignedVarIntProtoLoop(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writeUnsignedVarIntProtoLoop(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readUnsignedVarIntProtoLoop(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("unSignedVarIntProtoLoop wrote " + bytes);
		buf.clear();
	}
	
	private static void unsignedVarIntProto(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writeUnsignedVarIntProto(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readUnsignedVarIntProto(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("unsignedVarIntProto wrote " + bytes);
		buf.clear();
	}
	
	private static void signedVarIntUnwrapped(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writeSignedVarIntUnwrapped(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readSignedVarIntUnwrapped(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("signedVarIntUnwrapped wrote " + bytes);
		buf.clear();
	}
	
	private static void signedVarIntLoop(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writeSignedVarIntLoop(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readSignedVarIntLoop(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("signedVarIntLoop wrote " + bytes);
		buf.clear();
	}
	
	private static void signedVarIntProto(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writeSignedVarIntProto(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readSignedVarIntProto(buf);
			//final int got = EncodingUtils.readSignedVarIntProtoLoop(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("signedVarIntProto wrote " + bytes);
		buf.clear();
	}
	
	private static void signedVarIntProtoLoop(final int totalCalls, final ByteBuf buf) {
		int bytes = 0;
		for(int i=0; i<totalCalls; ++i) {
			final int value = values[i];
			final int startI = buf.writerIndex();
			EncodingUtils.writeSignedVarIntProtoLoop(value, buf);
			bytes += buf.writerIndex() - startI;
			final int got = EncodingUtils.readSignedVarIntProtoLoop(buf);
			Assertions.assertEquals(value, got);
		}
		System.out.println("signedVarIntProtoLoop wrote " + bytes);
		buf.clear();
	}
	
}
