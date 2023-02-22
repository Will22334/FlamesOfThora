package com.thora.server;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Objects;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.thora.core.HasLogger;
import com.thora.core.net.netty.EncodingUtils;
import com.thora.core.world.World;
import com.thora.server.command.CommandManager;

public abstract class ThoraServer implements HasLogger {
	
	public static final Path PATH_ASSETS_DIR = Paths.get("./assets");
	public static final Path PATH_KEYS_DIR = PATH_ASSETS_DIR.resolve("keys");
	
	public static enum Status {
		OFF(),
		STARTING(),
		ON(true),
		STOPPING();
		private final boolean up;
		private Status() {
			this(false);
		}
		public boolean isUp() {
			return up;
		}
		private Status(boolean up) {
			this.up = up;
		}
	}
	
	public static final KeyPair readKeyPair(Path dir) throws Exception {
		KeyPair keyIdentity = EncodingUtils.readKeyPair(dir.resolve("public.key"), dir.resolve("private.key"));
		return keyIdentity;
	}
	
	private static final Logger globalLogger = LogManager.getLogger(ThoraServer.class);
	
	public static Logger globalLogger() {
		return globalLogger;
	}
	
	private final KeyPair identity;
	protected Status status = Status.OFF;
	
	public ThoraServer(final KeyPair identity) {
		this.identity = Objects.requireNonNull(identity, "Cannot instantiate ThoraServer with null identity");
	}
	
	@Override
	public Logger logger() {
		return globalLogger();
	}
	
	public Status getStatus() {
		return status;
	}
	
	protected final KeyPair identity() {
		return identity;
	}
	
	public final PublicKey publicKey() {
		return identity().getPublic();
	}
	
	public abstract InetSocketAddress getListenSocket();
	
	public abstract void start(InetSocketAddress address) throws Exception;
	
	public abstract void stop();
	
	public abstract World getWorld();
	
	public abstract CommandManager commandManager();
	
}