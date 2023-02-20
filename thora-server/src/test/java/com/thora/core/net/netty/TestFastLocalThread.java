package com.thora.core.net.netty;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import io.netty.buffer.ByteBuf;
import io.netty.channel.DefaultEventLoop;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoop;
import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseCombiner;

@TestMethodOrder(value = OrderAnnotation.class)
@Execution(value = ExecutionMode.SAME_THREAD)
class TestFastLocalThread {

	public static final Logger logger = LogManager.getLogger("ThreadLocal");
	
	public static final Logger logger() {
		return logger;
	}
	
	public static final int THREADS = 2;
	public static final int iterations = 10;
	public static final int BUFFER_CALLS = 2;
	public static final int WRITE_CALLS = 4;
	public static final int BUFF_LENGTH = 4 * 1024;
	public static byte[] data = new byte[BUFF_LENGTH];
	
	private static EventLoopGroup eventLoopGroup;

	private static final EventLoop executor() {
		return eventLoopGroup.next();
	}

	@BeforeAll
	static void setUp() throws Exception {
		Random rand = new Random();
		rand.nextBytes(data);
		eventLoopGroup = new DefaultEventLoopGroup(THREADS);
		initBuffers();
	}
	
	private static void initBuffers() {
		Future<?>[] tasks = new Future[200];
		for(int i=0; i<tasks.length; ++i) {
			tasks[i] = executor().submit(() -> {
				EncodingUtils.tempBuf();
				EncodingUtils.fastTempBuf();
			});
		}
		for(Future<?> f : tasks) {
			f.awaitUninterruptibly();
		}
	}
	
	private static void doRecallLocal() {
		try {
			for(int i=0; i<BUFFER_CALLS; ++i) {
				final ByteBuf buf = EncodingUtils.tempBuf();
				for(int j=0; j<WRITE_CALLS; ++j) {
					buf.writeBytes(data);
				}
			}
		} catch(Throwable t) {
			logger().atWarn().withThrowable(t).log("Issue with normal ThreadLocal");
		}
	}
	
	private static void doSingleLocal() {
		try {
			final ByteBuf buf = EncodingUtils.tempBuf();
			for(int i=0; i<BUFFER_CALLS; ++i) {
				for(int j=0; j<WRITE_CALLS; ++j) {
					buf.writeBytes(data);
				}
				buf.clear();
			}
		} catch(Throwable t) {
			logger().atWarn().withThrowable(t).log("Issue with normal ThreadLocal");
		}
	}
	
	private static void doFastRecallLocal() {
		try {
			for(int i=0; i<BUFFER_CALLS; ++i) {
				final ByteBuf buf = EncodingUtils.fastTempBuf();
				for(int j=0; j<WRITE_CALLS; ++j) {
					buf.writeBytes(data);
				}
			}
		} catch(Throwable t) {
			logger().atWarn().withThrowable(t).log("Issue with normal ThreadLocal");
		}
	}
	
	private static void doFastSingleLocal() {
		try {
			final ByteBuf buf = EncodingUtils.fastTempBuf();
			for(int i=0; i<BUFFER_CALLS; ++i) {
				for(int j=0; j<WRITE_CALLS; ++j) {
					buf.writeBytes(data);
				}
				buf.clear();
			}
		} catch(Throwable t) {
			logger().atWarn().withThrowable(t).log("Issue with normal ThreadLocal");
		}
	}
	
	@Test
	@Order(3)
	void testLocalRecall() {
		
		EventExecutor ex = new DefaultEventLoop();
		Promise<Void> promise = ex.newPromise();
		promise.addListener(f -> {
			ex.shutdownGracefully();
		});
		PromiseCombiner fCombiner = new PromiseCombiner(ex);
		ex.execute(() -> {
			Future<?>[] tasks = new Future[iterations];
			for(int i=0; i<tasks.length; ++i) {
				tasks[i] = executor().submit(TestFastLocalThread::doRecallLocal);
				fCombiner.add(tasks[i]);
			}
			fCombiner.finish(promise);
		});
		
		promise.awaitUninterruptibly();
	}
	
	@Test
	@Order(2)
	void testLocalSingle() {
		
		EventExecutor ex = new DefaultEventLoop();
		Promise<Void> promise = ex.newPromise();
		promise.addListener(f -> {
			ex.shutdownGracefully();
		});
		PromiseCombiner fCombiner = new PromiseCombiner(ex);
		ex.execute(() -> {
			Future<?>[] tasks = new Future[iterations];
			for(int i=0; i<tasks.length; ++i) {
				tasks[i] = executor().submit(TestFastLocalThread::doSingleLocal);
				fCombiner.add(tasks[i]);
			}
			fCombiner.finish(promise);
		});
		
		promise.awaitUninterruptibly();
	}
	
	@Test
	@Order(5)
	void testFastRecallLocal() {
		
		EventExecutor ex = new DefaultEventLoop();
		Promise<Void> promise = ex.newPromise();
		promise.addListener(f -> {
			ex.shutdownGracefully();
		});
		PromiseCombiner fCombiner = new PromiseCombiner(ex);
		ex.execute(() -> {
			Future<?>[] tasks = new Future[iterations];
			for(int i=0; i<tasks.length; ++i) {
				tasks[i] = executor().submit(TestFastLocalThread::doFastRecallLocal);
				fCombiner.add(tasks[i]);
			}
			fCombiner.finish(promise);
		});
		
		promise.awaitUninterruptibly();
	}
	
	@Test
	@Order(5)
	void testFastSingleLocal() {
		
		EventExecutor ex = new DefaultEventLoop();
		Promise<Void> promise = ex.newPromise();
		promise.addListener(f -> {
			ex.shutdownGracefully();
		});
		PromiseCombiner fCombiner = new PromiseCombiner(ex);
		ex.execute(() -> {
			Future<?>[] tasks = new Future[iterations];
			for(int i=0; i<tasks.length; ++i) {
				tasks[i] = executor().submit(TestFastLocalThread::doFastSingleLocal);
				fCombiner.add(tasks[i]);
			}
			fCombiner.finish(promise);
		});
		
		promise.awaitUninterruptibly();
	}
	
}
