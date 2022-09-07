package com.thora.core.net.netty;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.ShortBufferException;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.NetworkSession;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;

public abstract class PodCodec<M> extends ByteToMessageCodec<M> {
	
	private final Map<Class<? extends M>,MessageEncoder<? extends M>> encoders = new HashMap<>();
	private final IntObjectMap<MessageDecoder<? extends M>> decoders = new IntObjectHashMap<>();
	
	protected final Logger logger;
	
	protected Logger logger() {
		return logger;
	}
	
	/**
	 * Provides a byte array used for buffering. The returned array may be pooled or not.
	 * The array is assumed to be empty on return disregarding any bytes already in the array.
	 * This methods behavior for nested calls is undefined.
	 */
	protected byte[] bufArray() {
		return EncodingUtils.bufferByteArray.get();
	}
	
	//public abstract NetworkProtocol<M> getNetworkProtocol();
	
	/**
	 * Returns {@code true} if and only if the specified message can be encoded by this codec.
	 *
	 * @param msg the message
	 */
	public boolean acceptOutboundMessage(Object msg) throws Exception {
		return encoders.containsKey(msg.getClass());
	}
	
	@Override
	protected void encode(ChannelHandlerContext ctx, M msg, ByteBuf out) throws Exception {
		MessageEncoder<? extends M> encoder = getEncoder(msg);
		if(encoder != null) {
			logger().trace("Using {} to encode {} to {}", encoder, msg, ctx);
			
			EncodingUtils.writeUByte(encoder.opcode, out);
			encoder.invoke(ctx, msg, out);
			
		} else {
			logger().warn("No encoder found for {} to {}", msg, ctx);
		}
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		long arrivalTime = System.currentTimeMillis();
		
		int initialRead = in.readerIndex();
		int length = in.readableBytes();
		
		int opcode = EncodingUtils.readUByte(in);
		
		MessageDecoder<? extends M> decoder = getDecoder(opcode);
		if(decoder != null) {
			
			logger().trace("Using {} to decode packet opcode {} from {}", decoder, opcode, ctx);
			
			try {
				M packet = decoder.decode(ctx, in);
				if(packet != null) {
					
					if(in.readableBytes() > 0) {
						throw new IOException(String.format("Decoded packet with %d bytes left over!", in.readableBytes()));
					}
					
					postDecode(ctx, packet, arrivalTime);
					out.add(packet);
				}
			} catch(Throwable t) {
				if(t instanceof IndexOutOfBoundsException || t instanceof IOException) {
					//Handle invalid binary encoding
					int relativeIndex = in.readerIndex() - initialRead;
					logger().warn(String.format("Encountered Exception while decoding %s\tfrom %s\tindex:%d=%s\tpayload:\n%s",
							decoder.getMessageName(), ctx.channel(), relativeIndex, EncodingUtils.getPrettyHexTableIndex(relativeIndex),
							ByteBufUtil.prettyHexDump(in, initialRead, length), t),
							t);
					discard(in);
				}
				throw t;
			}
			
		} else {
			int payloadSize = in.readableBytes();
			logger().warn("No decoder found for opcode={}, with payload size={}, from {}",
					opcode, payloadSize, ctx);
			discard(in);
		}
	}
	
	public static void discard(ByteBuf buf) {
		buf.skipBytes(buf.readableBytes());
	}
	
	protected void postDecode(ChannelHandlerContext ctx, M message, long arrivalTime) {
		
	}
	
	@SuppressWarnings("unchecked")
	protected final <P extends M> MessageEncoder<P> getEncoder(P packet) {
		return (PodCodec<M>.MessageEncoder<P>) encoders.get(packet.getClass());
	}
	
	protected final boolean addEncoder(MessageEncoder<? extends M> encoder) {
		return encoders.putIfAbsent(encoder.messageClass, encoder) == null;
	}
	
	protected final MessageEncoder<? extends M> removeEncoder(M mesageClass) {
		return encoders.remove(mesageClass);
	}
	
	@SuppressWarnings("unchecked")
	protected final <P extends M> MessageDecoder<P> getDecoder(int opcode) {
		return (PodCodec<M>.MessageDecoder<P>) decoders.get(opcode);
	}
	
	protected final boolean addDecoder(MessageDecoder<? extends M> decoder) {
		return decoders.putIfAbsent(decoder.opcode(), decoder) != null;
	}
	
	protected final MessageDecoder<? extends M> removeDecoder(int opcode) {
		return decoders.remove(opcode);
	}
	
	@Override
	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		populate();
		super.handlerAdded(ctx);
	}
	
	@Override
	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		encoders.clear();
		decoders.clear();
		super.handlerRemoved(ctx);
	}
	
	protected NetworkSession getSession(ChannelHandlerContext ctx) {
		return null;
	}
	
	protected Cipher getPublicDecryptCipher() {
		return null;
		//return server.publicDecCipher;
	}
	
	public abstract class MessageEncoder<O extends M> {
		
		protected final Class<O> messageClass;
		private final int opcode;
		
		protected final int opcode() {
			return opcode;
		}
		
		protected final Class<O> messageClass() {
			return messageClass;
		}
		
		@SuppressWarnings("unchecked")
		public void invoke(ChannelHandlerContext ctx, M m, ByteBuf buf) {
			encode(ctx, (O) m, buf);
		}
		
		public abstract void encode(ChannelHandlerContext ctx, O obj, ByteBuf buf);
		
		@SuppressWarnings("unchecked")
		private Class<O> findPacketClass() {
			Class<O> genericClasss = (Class<O>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
			return genericClasss;
		}
		
		public PodCodec<M> getCodec() {
			return PodCodec.this;
		}
		
		@Override
		public String toString() {
			return "Encoder[" + messageClass().getSimpleName() + "]";
		}
		
		protected MessageEncoder(int opCode) {
			this.opcode = opCode;
			this.messageClass = findPacketClass();
		}
		
	}
	
	public abstract class MessageDecoder<P extends M> {
		private final int opcode;
		private final Class<P> messageClass;
		
		protected final int opcode() {
			return opcode;
		}
		
		public final Class<P> getMessageClass() {
			return messageClass;
		}
		
		public String getMessageName() {
			return getMessageClass().getSimpleName();
		}
		
		public abstract P decode(ChannelHandlerContext ctx, ByteBuf buf) throws IOException;
		
		protected void discard(ByteBuf buf) {
			PodCodec.discard(buf);
		}
		
		public PodCodec<M> getCodec() {
			return PodCodec.this;
		}
		
		@SuppressWarnings("unchecked")
		private Class<P> findMessageClass() {
			Class<P> genericClasss = (Class<P>) ((ParameterizedType) getClass().getGenericSuperclass())
					.getActualTypeArguments()[0];
			return genericClasss;
		}
		
		public MessageDecoder(int opcode) {
			this.opcode = opcode;
			this.messageClass = findMessageClass();
		}
		
	}
	
	public abstract class EncryptedPayloadMessageDecoder<K extends M> extends MessageDecoder<K> {
		
		@Override
		public K decode(ChannelHandlerContext ctx, ByteBuf enc) throws IOException {
			//TODO length is still included in read buf and rest of message header/payload.
			
			int length = enc.readableBytes();
			ByteBuf plain = ctx.alloc().buffer(length);
			
			try {
				plain.writeBytes(enc, length);
				EncodingUtils.decryptSameFast(plain, getPublicDecryptCipher());
				return decodePlain(ctx, plain);
				
			} catch (IllegalBlockSizeException | BadPaddingException | ShortBufferException e) {
				
				throw EncodingUtils.wrapIO(e);
				
			} finally {
				// Meant for releasing local ByteBuf
				plain.release();
			}
		}
		
		protected abstract K decodePlain(ChannelHandlerContext ctx, ByteBuf buf);
		
		public EncryptedPayloadMessageDecoder(int opcode) {
			super(opcode);
		}
		
	}
	
	public abstract class EncryptedPayloadMessageEncoder<K extends M> extends MessageEncoder<K> {
		
		public abstract void encodePlain(ChannelHandlerContext ctx, K packet, ByteBuf buf);
		
		protected Cipher getCipher(ChannelHandlerContext ctx, NetworkSession session) {
			return session.getCryptoCreds().getSymmetric().getEncryptCipher();
		}
		
		public void encode(ChannelHandlerContext ctx, K packet, ByteBuf buf) {
			NetworkSession session = getSession(ctx);
			int initialRead = buf.readerIndex();
			EncodingUtils.writeUByte(opcode(), buf);
			int initialWrite = buf.writerIndex();
			buf.readerIndex(initialWrite);
			encodePlain(ctx, packet, buf);
			try {
				EncodingUtils.encryptSame(buf, getCipher(ctx, session));
			} catch (BadPaddingException | IllegalBlockSizeException e) {
				logger().atWarn().withThrowable(e).log("Exception while encrypting payload!");
			}
			buf.readerIndex(initialRead);
		}
		
		EncryptedPayloadMessageEncoder(int opCode) {
			super(opCode);
		}
		
	}
	
	protected abstract void populate();
	
	public PodCodec(Logger logger) {
		this.logger = logger;
	}
	
}