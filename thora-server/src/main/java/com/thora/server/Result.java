package com.thora.server;

public class Result<E> extends SimpleResult {
	
	public static final <E> Result<E> success(E element, String message) {
		return new Result<>(true, element, message);
	}
	
	public static final <E> Result<E> fail(E element, String message) {
		return new Result<>(false, element, message);
	}
	
	public static final <E> Result<E> fail(String message) {
		return new Result<>(false, null, message);
	}
	
	public static final <E> Result<E> fail(SimpleResult r) {
		return fail(null, r.getMessage());
	}
	
	private final E element;
	
	public final E get() {
		return element;
	}
	
	public Result(boolean result, E element, String message) {
		super(result, message);
		this.element = element;
	}
	
	public Result(E element, String message) {
		this(element != null, element, message);
	}
	
}
