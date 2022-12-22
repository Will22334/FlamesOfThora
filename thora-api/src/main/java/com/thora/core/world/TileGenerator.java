package com.thora.core.world;

import java.util.Objects;
import java.util.Random;

/**
 * Generates a {@link TileType} for a given {@link Location} through {@link #getTileType(Location)}.
 * This interface should only be used by {@link AbstractWorld} instances.
 * @author Dave
 *
 */
public interface TileGenerator {
	
	public static final EmptyTileGenerator EMPTY = new EmptyTileGenerator();
	
	public static final class EmptyTileGenerator implements TileGenerator {
		@Override
		public final boolean isEmpty() {
			return true;
		}
		@Override
		public Material getTileType(int x, int y) {
			return Material.VOID;
		}
	}
	
	public static class FillTileGenerator implements TileGenerator {
		private final Material type;
		public FillTileGenerator(Material type) {
			this.type = Objects.requireNonNull(type, "TileType cannot be null!");
		}
		@Override
		public Material getTileType(int x, int y) {
			return type;
		}
	}
	
	public static final class RandomTileGenerator implements TileGenerator {
		private final Random rand;
		public RandomTileGenerator(Random rand) {
			this.rand = rand;
		}
		@Override
		public Material getTileType(int x, int y) {
			return Material.random(rand);
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
	default Material getTileType(ILocatable point) {
		return getTileType(point.getX(), point.getY());
	}
	
	/**
	 * Returns the {@link TileType} for given (x,y) coordinates.
	 * @param x
	 * @param y
	 * @return tile type for that location
	 */
	Material getTileType(int x, int y);
	
}
