package com.thora.server;

public class SimpleResult {
	
	public static final SimpleResult successSimple(String message) {
		return new SimpleResult(true, message);
	}
	
	public static final SimpleResult failSimple(String message) {
		return new SimpleResult(false, message);
	}
	
	protected final boolean result;
	protected final String message;
	
	public final boolean isSuccess() {
		return result;
	}
	
	public final boolean isFail() {
		return !isSuccess();
	}
	
	public final String getMessage() {
		return message;
	}
	
	public SimpleResult(boolean result, String message) {
		this.result = result;
		this.message = message;
	}
	
}
