package com.thora.server;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GameEngine {
	
	protected final Logger logger;
	
	protected final BlockingQueue<Runnable> eventQueue = new LinkedBlockingQueue<>();
	
	protected ScheduledExecutorService executor;
	protected final int sideThreads;
	
	protected boolean done = false;
	
	private void process() {
		Runnable event = null;
		//long last = System.currentTimeMillis();
		while(!done) {
			
			try {
				
				while((event = eventQueue.poll(750, TimeUnit.MILLISECONDS)) != null) {
					event.run();
				}
				
			} catch (Throwable t) {
				if(t instanceof InterruptedException) {
					if(done) break;
				}
				logger().atWarn().withThrowable(t).log("Exception thrown while executing {}", event);
			}
			
		}
		executor.shutdown();
		eventQueue.clear();
		executor = null;
	}
	
	private Thread coreThread = new Thread(this::process);
	
	protected BlockingQueue<Runnable> queue() {
		return eventQueue;
	}
	
//	protected final ForkJoinPool pool() {
//		return pool;
//	}
	
	public final ScheduledExecutorService executor() {
		return executor;
	}
	
	protected final Logger logger() {
		return logger;
	}
	
	public boolean isRunning() {
		return !done && executor != null;
	}
	
	public void start() {
		executor = new ScheduledThreadPoolExecutor(this.sideThreads);
		done = false;
		coreThread.start();
	}
	
	public void stop() {
		done = true;
		coreThread.interrupt();
	}
	
	public <T> Future<T> submit(Runnable task) {
		return enqueue(new FutureTask<>(task, null));
	}
	
	public <T> Future<T> submit(Callable<T> task) {
		return enqueue(new FutureTask<>(task));
	}
	
	public void execute(Runnable task) {
		enqueue(task);
	}
	
	protected <T extends Runnable> T enqueue(T task) {
		queue().offer(task);
		return task;
	}
	
	private String stringVal = null;
	
	@Override
	public String toString() {
		if(stringVal == null) {
			stringVal = String.format("%s[%s]", getClass().getSimpleName(), sideThreads);
		}
		return stringVal;
	}
	
	public GameEngine(int sideThreads, Logger logger) {
		this.logger = logger;
		this.sideThreads = sideThreads;
	}
	
	public GameEngine(int sideThreads) {
		this(sideThreads, LogManager.getLogger(GameEngine.class));
	}
	
}