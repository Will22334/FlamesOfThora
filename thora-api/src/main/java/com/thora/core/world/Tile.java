package com.thora.core.world;

import java.util.function.Predicate;

public interface Tile extends Locatable {
	
	@Override
	public default Tile getTile() {
		return this;
	}
	
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
	public default boolean isInWalkingRange(Locatable l, double walkRange) {
		return getOrthogonallDistance(l) <= walkRange;
	}
	
	public default Predicate<Tile> inWalkingRangePred(double walkRange) {
		return (t) -> this.isInWalkingRange(t, walkRange);
	}
	
	public static Predicate<Double> inWalkingRangePred(Locatable l) {
		return (r) -> l.isInWalkingRange(l, r);
	}
	
}
