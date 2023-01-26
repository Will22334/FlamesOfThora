package com.thora.core.net.netty;

import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.ParameterizedMessage;

import com.thora.core.net.NetworkSession;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public abstract class PodHandler<M> extends SimpleChannelInboundHandler<M> {
	
	private final Logger logger;
	
	protected final Logger logger() {
		return logger;
	}
	
	protected final Map<Class<? extends M>,MessageConsumer<? extends M>> handlers = new HashMap<>();
	
	protected PodHandler(Logger logger) {
		this.logger = logger;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void channelRead0(final ChannelHandlerContext ctx, final M message) throws Exception {
		
		final Class<? extends M> messageClass = (Class<? extends M>) message.getClass();
		final MessageConsumer<? extends M> consumer = handlers.get(messageClass);
		if(consumer != null) {
			onHandlerFound(ctx, messageClass, message);
			dispatch(ctx, consumer, message);
		} else {
			onNoHandlerFound(ctx, messageClass, message);
		}
		
	}
	
	protected <P extends M> void dispatch(final ChannelHandlerContext ctx, final MessageConsumer<P> consumer, final M message) {
		consumer.invoke(ctx, message);
	}
	
	protected <P extends M> void onHandlerFound(final ChannelHandlerContext ctx, final Class<P> messageClass, final M message) {
		logger().atLevel(Level.TRACE).log(() -> {
			return new ParameterizedMessage("Handler found for {} from {}", prettyMessage(message), NetworkSession.findSession(ctx));
		});
	}
	
	protected <P extends M> void onNoHandlerFound(final ChannelHandlerContext ctx, final Class<P> messageClass, final M message) {
		logger().atLevel(Level.WARN).log(() -> {
			return new ParameterizedMessage("No handler for {} from {}", prettyMessage(message), NetworkSession.findSession(ctx));
		});
	}
	
	public static final <P> String prettyMessage(final P message) {
		if(message == null) return null;
		return message.getClass().getSimpleName();
	}
	
	protected abstract void populate();
	
	protected <P extends M> void addHandler(MessageConsumer<P> consumer) {
		handlers.put(consumer.getMessageClass(), consumer);
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		this.populate();
	}

	protected abstract class MessageConsumer<P extends M> {
		
		protected final Class<P> messageClass;
		
		public MessageConsumer(final int index) {
			this.messageClass = findmessageClass(index);
		}
		
		public MessageConsumer() {
			this(0);
		}
		
		public final Class<P> getMessageClass() {
			return messageClass;
		}
		
		@SuppressWarnings("unchecked")
		public final void invoke(final ChannelHandlerContext ctx, final M message) {
			consume(ctx, (P) message);
		}
		
		public abstract void consume(final ChannelHandlerContext ctx, final P message);
		
		@SuppressWarnings("unchecked")
		private Class<P> findmessageClass(final int index) {
			return (Class<P>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[index];
		}

		@Override
		public String toString() {
			return getClass().getSimpleName() + "[" + getMessageClass().getSimpleName() + "]";
		}
		
	}
	
	protected abstract class SessionMessageConsumer<P extends M, S extends NetworkSession> extends MessageConsumer<P> {
		
		public SessionMessageConsumer() {
			
		}
		
		@Override
		public final void consume(ChannelHandlerContext ctx, P message) {
			consume(ctx, NetworkSession.findSession(ctx), message);
		}
		
		public abstract void consume(ChannelHandlerContext ctx, S session, P message);
		
	}
	
}
