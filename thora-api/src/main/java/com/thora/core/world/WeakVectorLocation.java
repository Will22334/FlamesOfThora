package com.thora.core.world;

import java.lang.ref.WeakReference;

import com.thora.core.math.BasicIntVector;
import com.thora.core.math.IntVector;


public class WeakVectorLocation<W extends World> extends IntVectorLocation {
	
	public static <W extends World> WeakVectorLocation<W> create(W world, IntVector v) {
		return new WeakVectorLocation<>(world, v);
	}
	
	public static <W extends World> WeakVectorLocation<W> create(W world, int x, int y) {
		return create(world, new BasicIntVector(x, y));
	}
	
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

	@Override
	public double[] comps() {
//		double[] arr = new double[2];
//		this.vector().putComps(arr);
//		return arr;
		return new double[] {v.getX(), v.getY()};
	}
	
	public int[] intComps() {
		return new int[] {v.getIX(), v.getIY()};
	}
	
}
