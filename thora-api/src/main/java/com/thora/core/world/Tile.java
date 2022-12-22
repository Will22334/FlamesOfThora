package com.thora.core.world;

import java.util.function.Predicate;

public interface Tile extends ILocatable {
	
	public TileData getTileData();
	
	public TileData setTileData(TileData tileData);
	
	public default Material getMaterial() {
		TileData data = getTileData();
		if(data == null) data = TileData.VOID;
		return data.material();
	}
	
	public default void setMaterial(Material material) {
		this.setTileData(new BasicTileData(material));
	}
	
	@Override
	public default boolean isInWalkingRange(ILocatable l, double walkRange) {
		return getWalkingDistance(l) <= walkRange;
	}
	
	public default Predicate<Tile> inWalkingRangePred(double walkRange) {
		return (t) -> this.isInWalkingRange(t, walkRange);
	}
	
	public static Predicate<Double> inWalkingRangePred(ILocatable l) {
		return (r) -> l.isInWalkingRange(l, r);
	}
	
}
