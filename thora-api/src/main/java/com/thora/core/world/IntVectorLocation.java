package com.thora.core.world;

import java.lang.ref.WeakReference;

import com.thora.core.math.IntArrVector;
import com.thora.core.math.IntVector;

public abstract class IntVectorLocation extends Location {
	
	public static class IntVectorRefLocation<W extends AbstractWorld> extends IntVectorLocation {
		
		private WeakReference<W> worldRef;
		
		public IntVectorRefLocation(W world, int x, int y) {
			super(x, y);
			this.worldRef = new WeakReference<>(world);
		}
		
		public IntVectorRefLocation(int x, int y) {
			this(null, x, y);
		}

		public IntVectorRefLocation(W world, IntArrVector v) {
			super(v);
			this.worldRef = new WeakReference<>(world);
		}
		
		public IntVectorRefLocation(W world, IntVector v) {
			super(new IntArrVector(v.getIX(), v.getIY()));
			this.worldRef = new WeakReference<>(world);
		}

		@Override
		public W getWorld() {
			return worldRef.get();
		}

		@Override
		public IntVectorRefLocation<W> clone() {
			return new IntVectorRefLocation<W>(getWorld(), this.v.clone());
		}
		
	}
	
	protected IntArrVector v;
	
	public IntVectorLocation(int x, int y) {
		this(new IntArrVector(x, y));
	}
	
	public IntVectorLocation(IntArrVector v) {
		this.v = v;
	}
	
	public IntVectorLocation(IntVector v) {
		this.v = new IntArrVector(v.getIX(), v.getIY());
	}
	
	public abstract AbstractWorld getWorld();
	
	public IntArrVector vector() {
		return v;
	}
	
	@Override
	public final int getX() {
		return vector().getIX();
	}
	
	@Override
	public final int getY() {
		return vector().getIY();
	}
	
	@Override
	public IntVectorLocation setAs(int x, int y) {
		this.v.setAs(x, y);
		return this;
	}
	
	public IntVectorLocation setAs(IntArrVector v) {
		this.v.setAs(v);
		return this;
	}
	
	@Override
	public IntVectorLocation getLocation() {
		return this;
	}
	
	/**
	 * Shifts this location by given vector.
	 * @param v
	 * @return This Location for chaining.
	 */
	public IntVectorLocation shift(IntVector v) {
		return shift(v.getIX(), v.getIY());
	}
	
	@Override
	public IntVectorLocation shift(int dx, int dy) {
		return this.setAs(getX() + dx, getY() + dy);
	}
	
	
	
	@Override
	public IntArrVector asVector() {
		return vector();
	}

	@Override
	public double[] comps() {
		return new double[] {getX(), getY()};
	}
	
	public int[] compsI() {
		return v.comps();
	}
	
	@Override
	public String toString() {
		return "[" + getX() + "," + getY() + "]";
	}
	
	@Override
	public int hashCode() {
		return 31 * (31 + getX()) + getY();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Location) {
			Location l = (Location) obj;
			return getX() == l.getX() &&
					getY() == l.getY();
		}
		return false;
	}
	
	@Override
	public abstract IntVectorLocation clone();
	
}
