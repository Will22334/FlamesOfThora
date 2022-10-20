package com.thora.core.world;

import java.util.Objects;
import java.util.Random;

/**
 * Generates a {@link TileType} for a given {@link Location} through {@link #getTileType(Location)}.
 * This interface should only be used by {@link World} instances.
 * @author Dave
 *
 */
public interface TileGenerator {
	
	public static final class EmptyTileGenerator implements TileGenerator {
		@Override
		public final boolean isEmpty() {
			return true;
		}
		@Override
		public TileType getTileType(int x, int y) {
			return TileType.VOID;
		}
	}
	
	public static class FillTileGenerator implements TileGenerator {
		private final TileType type;
		public FillTileGenerator(TileType type) {
			this.type = Objects.requireNonNull(type, "TileType cannot be null!");
		}
		@Override
		public TileType getTileType(int x, int y) {
			return type;
		}
	}
	
	public static final class RandomTileGenerator implements TileGenerator {
		private final Random rand;
		public RandomTileGenerator(Random rand) {
			this.rand = rand;
		}
		@Override
		public TileType getTileType(int x, int y) {
			return TileType.random(rand);
		}
	}
	
	default boolean isEmpty() {
		return false;
	}
	
	/**
	 * Returns the {@link TileType} for a given {@link Location}.
	 * @param point - The tile location
	 * @return the tile type
	 */
	default TileType getTileType(Locatable point) {
		return getTileType(point.getX(), point.getY());
	}
	
	/**
	 * Returns the {@link TileType} for given (x,y) coordinates.
	 * @param x
	 * @param y
	 * @return tile type for that location
	 */
	TileType getTileType(int x, int y);
	
}
