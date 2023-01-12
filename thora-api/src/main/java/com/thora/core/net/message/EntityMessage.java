package com.thora.core.net.message;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.thora.core.world.WorldEntity;

import io.netty.util.collection.IntCollections;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

public class EntityMessage extends AbstractThoraMessage {
	
	public static enum UpdateType {
		CREATE(),
		UPDATE(),
		DESTROY();
	}
	
	public static class EntityCreate {
		public WorldEntity entity;
	}
	
	public static class EntityUpdate {
		public WorldEntity entity;
	}
	
	public static class EntityDestroy {
		public int entityID;
	}
	
	private IntObjectMap<WorldEntity> allCreate = null;
	private IntObjectMap<WorldEntity> allUpdate = null;
	private Collection<Integer> allDestroy = null;
	
	public IntObjectMap<WorldEntity> getCreate() {
		if(allCreate == null) {
			return IntCollections.emptyMap();
		}
		return allCreate;
	}
	
	public EntityMessage addCreate(WorldEntity e) {
		if(allCreate == null) {
			allCreate = new IntObjectHashMap<>();
		}
		allCreate.put(e.getID(), e);
		return this;
	}
	
	public IntObjectMap<WorldEntity> getUpdate() {
		if(allUpdate == null) {
			return IntCollections.emptyMap();
		}
		return allUpdate;
	}
	
	public EntityMessage addUpdate(WorldEntity e) {
		if(allUpdate == null) {
			allUpdate = new IntObjectHashMap<>();
		}
		allUpdate.put(e.getID(), e);
		return this;
	}
	
	public Collection<Integer> getDestroy() {
		if(allDestroy == null) {
			return Collections.emptySet();
		}
		return allDestroy;
	}
	
	public EntityMessage addDestroy(int id) {
		if(allDestroy == null) {
			allDestroy = new HashSet<>();
		}
		allDestroy.add(id);
		return this;
	}
	
}
