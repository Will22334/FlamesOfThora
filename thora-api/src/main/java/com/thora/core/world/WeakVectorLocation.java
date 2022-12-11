package com.thora.core.world;

import java.lang.ref.WeakReference;

import com.thora.core.math.IntVector;


public class WeakVectorLocation<W extends World> extends IntVectorLocation {
	
	protected WeakReference<W> worldRef;
	
	public WeakVectorLocation(W world, IntVector v) {
		super(v);
		this.worldRef = new WeakReference<>(world);
	}
	
	public WeakVectorLocation(W world, int x, int y) {
		super(x, y);
		this.worldRef = new WeakReference<>(world);
	}
	
	public WeakVectorLocation(int x, int y) {
		this(null, x, y);
	}
	
	WeakVectorLocation<W> setWorld(W world) {
		this.worldRef = new WeakReference<>(world);
		return this;
	}
	
	@Override
	public W getWorld() {
		return worldRef.get();
	}
	
	@Override
	public WeakVectorLocation<W> clone() {
		return new WeakVectorLocation<W>(getWorld(), vector().clone());
	}
	
	@Override
	public WeakVectorLocation<W> setAs(int x, int y) {
		vector().setAs(x, y);
		return this;
	}
	
}
