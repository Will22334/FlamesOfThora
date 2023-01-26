package com.thora.core.net.netty;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

import org.apache.logging.log4j.Logger;

import com.thora.core.net.AsymmetricKeyCipher;
import com.thora.core.net.NetworkSession;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
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
	
	@Override
	protected void encode(ChannelHandlerContext ctx, M msg, ByteBuf out) throws Exception {
		MessageEncoder<? extends M> encoder = getEncoder(msg);
		if(encoder != null) {
			//logger().trace("Encoding {} to {}", () -> msg.getClass().getSimpleName(), () -> ctx.channel());
			
			EncodingUtils.writeUByte(encoder.opcode, out);
			encoder.invoke(ctx, msg, out);
			
		} else {
			EncoderException e = new EncoderException("No encoder found for " + msg.getClass().getSimpleName() + " while encoding to " + ctx);
			logger().atWarn().withThrowable(e).log();
			throw e;
		}
	}
	
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		final long arrivalTime = System.currentTimeMillis();
		
		final int initialRead = in.readerIndex();
		final int length = in.readableBytes();
		
		final int opcode = EncodingUtils.readUByte(in);
		
		final MessageDecoder<? extends M> decoder = getDecoder(opcode);
		if(decoder != null) {
			
			//logger().trace("Using {} to decode packet opcode[{}] from {}", decoder, opcode, ctx);
			
			try {
				final M packet = decoder.decode(ctx, in);
				if(packet != null) {
					
					if(in.readableBytes() > 0) {
						throw new DecoderException(String.format("Decoded %s but with %d unread bytes in frame!", packet, in.readableBytes()));
					}
					
					postDecode(ctx, packet, arrivalTime);
					out.add(packet);
				}
			} catch(Throwable t) {
				if(t instanceof IndexOutOfBoundsException || t instanceof DecoderException || t instanceof IOException) {
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
			int payloadSize = length - 1;
			logger().warn("No decoder found for opcode={}, with payload size={}, from {}",
					opcode, payloadSize, ctx);
			discard(in);
		}
	}
	
	public static void discard(final ByteBuf buf) {
		buf.skipBytes(buf.readableBytes());
	}
	
	protected void postDecode(final ChannelHandlerContext ctx, final M message, final long arrivalTime) {
		
	}
	
	@SuppressWarnings("unchecked")
	protected final <P extends M> MessageEncoder<P> getEncoder(final P packet) {
		return (PodCodec<M>.MessageEncoder<P>) encoders.get(packet.getClass());
	}
	
	protected final boolean addEncoder(final MessageEncoder<? extends M> encoder) {
		return encoders.putIfAbsent(encoder.messageClass, encoder) == null;
	}
	
	protected final MessageEncoder<? extends M> removeEncoder(final M mesageClass) {
		return encoders.remove(mesageClass);
	}
	
	@SuppressWarnings("unchecked")
	protected final <P extends M> MessageDecoder<P> getDecoder(final int opcode) {
		return (PodCodec<M>.MessageDecoder<P>) decoders.get(opcode);
	}
	
	protected final <P extends M> boolean addDecoder(final MessageDecoder<P> decoder) {
		return decoders.putIfAbsent(decoder.opcode(), decoder) == null;
	}
	
	protected final MessageDecoder<? extends M> removeDecoder(final int opcode) {
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
	
	protected abstract NetworkSession getSession(ChannelHandlerContext ctx);
	
	protected AsymmetricKeyCipher handshakeCipher;
	
	protected AsymmetricKeyCipher getHandShakeCipher() {
		return handshakeCipher;
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
		public void invoke(ChannelHandlerContext ctx, M msg, ByteBuf buf) {
			encode(ctx, (O) msg, buf);
		}
		
		public abstract void encode(ChannelHandlerContext ctx, O msg, ByteBuf buf);
		
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
		
		@Override
		public String toString() {
			return getClass().getSimpleName();
		}
		
		public MessageDecoder(int opcode) {
			this.opcode = opcode;
			this.messageClass = findMessageClass();
		}
		
	}
	
	public abstract class EncryptedPayloadMessageDecoder<K extends M> extends MessageDecoder<K> {
		
		@Override
		public K decode(final ChannelHandlerContext ctx, final ByteBuf enc) throws IOException {
			//TODO length is still included in read buf and rest of message header/payload.
			final NetworkSession session = getSession(ctx);
			
			final int length = enc.readableBytes();
			final ByteBuf plain = ctx.alloc().buffer(length);
			
			try {
				plain.writeBytes(enc, length);
				//EncodingUtils.decryptSame(plain, session.getCryptoCreds().decrypt());
				EncodingUtils.decryptSameByteArrayBuf(plain, plain.readableBytes(), session.getCryptoCreds().decrypt());
				return decodePlain(ctx, plain);
				
			} catch (IllegalBlockSizeException | BadPaddingException e) {
				
				throw EncodingUtils.wrapIO("Exception while decrypting " + getMessageName() + " from " + ctx, e);
				
			} finally {
				// Meant for releasing local ByteBuf
				plain.release();
			}
		}
		
		protected abstract K decodePlain(final ChannelHandlerContext ctx, final ByteBuf buf);
		
		public EncryptedPayloadMessageDecoder(final int opcode) {
			super(opcode);
		}
		
	}
	
	public abstract class EncryptedPayloadMessageEncoder<K extends M> extends MessageEncoder<K> {
		
		public abstract void encodePlain(final ChannelHandlerContext ctx, final K packet, final ByteBuf buf);
		
		protected Cipher getCipher(final ChannelHandlerContext ctx, final NetworkSession session) {
			return session.getCryptoCreds().getSymmetric().getEncryptCipher();
		}
		
		public void encode(final ChannelHandlerContext ctx, final K packet, final ByteBuf buf) {
			final NetworkSession session = getSession(ctx);
			int initialRead = buf.readerIndex();
			//EncodingUtils.writeUByte(opcode(), buf);
			int payloadHeadIndex = buf.writerIndex();
			buf.readerIndex(payloadHeadIndex);
			encodePlain(ctx, packet, buf);
			try {
				EncodingUtils.encryptSame(buf, getCipher(ctx, session));
			} catch (BadPaddingException | IllegalBlockSizeException e) {
				logger().atWarn().withThrowable(e).log("Exception while encrypting payload using {}", this);
			}
			buf.readerIndex(initialRead);
		}
		
		protected EncryptedPayloadMessageEncoder(int opCode) {
			super(opCode);
		}
		
	}
	
	protected abstract void populate();
	
	public PodCodec(Logger logger) {
		this.logger = logger;
	}
	
}