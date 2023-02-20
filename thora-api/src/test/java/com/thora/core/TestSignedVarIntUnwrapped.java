package com.thora.core;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestSignedVarIntUnwrapped {
	
	private static final Logger logger = LogManager.getLogger(TestSignedVarIntUnwrapped.class);
	
	private static final Random rand = new Random();
	private static final int totalCalls = 50_000_000;
	private static final int[] values;
	
	static {
		values = new int[totalCalls];
		for(int i=0; i<totalCalls; ++i) {
			//values[i] = Math.abs(rand.nextInt());
			values[i] = Math.abs(rand.nextInt() / 10);
			//values[i] = rand.nextInt() / 10;
		}
	}
	
	public static void main(String[] args) {
		
		
		
	}
	
	
	
}
