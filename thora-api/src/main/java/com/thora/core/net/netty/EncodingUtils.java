package com.thora.core.net.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.ShortBufferException;
import javax.crypto.spec.SecretKeySpec;

import com.thora.core.Utils;
import com.thora.core.Utils.IntIntObjFunction;
import com.thora.core.Utils.IntObjFunction;
import com.thora.core.Utils.IntObjObjConsumer;
import com.thora.core.Utils.IntObjObjFunction;
import com.thora.core.Utils.TriConsumer;
import com.thora.core.Utils.TriFunction;
import com.thora.core.net.HasCryptographicCredentials;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ByteProcessor;
import io.netty.util.collection.IntObjectHashMap;
import io.netty.util.collection.IntObjectMap;
import io.netty.util.collection.IntObjectMap.PrimitiveEntry;
import io.netty.util.concurrent.FastThreadLocal;

public class EncodingUtils {
	
	public static final String REGEX_SOCKET_ADDRESS = "^\\s*(?<host>.*)\\:(?<port>\\d+)\\s*$";
	public static final Pattern PATTERN_SOCKET_ADDRESS = Pattern.compile(REGEX_SOCKET_ADDRESS);
	
	public static final int TEMP_BYTE_BUFFER_SIZE = 4 * 1024;
	
	private static final FastThreadLocal<ByteBuf> fastTempByteBuf = new FastThreadLocal<ByteBuf>() {
		@Override
		protected ByteBuf initialValue() throws Exception {
			return EncodingUtils.allocNewBuf();
		}
		@Override
		protected void onRemoval(ByteBuf value) throws Exception {
			value.release();
		}
	};
	
	private static final ThreadLocal<ByteBuffer> tempByteBuffer = ThreadLocal.withInitial(EncodingUtils::newTempByteBuffer);
	private static final ThreadLocal<ByteBuf> tempByteBuf = ThreadLocal.withInitial(lazyAllocDirect(TEMP_BYTE_BUFFER_SIZE));
	
	public static final int TEMP_BYTE_ARRAY_SIZE = 8 * 1024;
	public static final ThreadLocal<byte[]> bufferByteArray = ThreadLocal.withInitial(EncodingUtils::newTempByteArray);
	
	private static final ByteBuffer newTempByteBuffer() {
		return ByteBuffer.allocateDirect(TEMP_BYTE_BUFFER_SIZE);
	}
	
	private static final ByteBuf allocNewBuf() {
		return Unpooled.directBuffer(TEMP_BYTE_BUFFER_SIZE);
	}
	
	private static final Supplier<ByteBuf> lazyAllocDirect(int initCapacity) {
		return Utils.bindArg(Unpooled::directBuffer, initCapacity);
	}
	
	private static final byte[] newTempByteArray() {
		return new byte[TEMP_BYTE_ARRAY_SIZE];
	}
	
	protected static ByteBuffer tempBuffer() {
		ByteBuffer buffer = tempByteBuffer.get();
		buffer.clear();
		return buffer;
	}
	
	public static ByteBuf fastTempBuf() {
		return fastTempByteBuf.get().clear();
	}
	
	public static ByteBuf tempBuf() {
		return tempByteBuf.get().clear();
	}
	
	protected static byte[] tempArray() {
		return bufferByteArray.get();
	}
	
	protected static byte[] tempArray(final int minSize) {
		if(minSize > TEMP_BYTE_ARRAY_SIZE) {
			return new byte[minSize];
		}
		return tempArray();
	}
	
	public static InetSocketAddress parseSocketAddress(String text) {
		Matcher m = PATTERN_SOCKET_ADDRESS.matcher(text);
		if(!m.matches()) return null;
		return new InetSocketAddress(m.group("host"), Integer.parseInt(m.group("port")));
	}
	
	private static KeyGenerator secretKeyGenerator;
	
	public static SecretKey generateNewSecretKey() {
		return secretKeyGenerator.generateKey();
	}
	
	public static final PrivateKey readPrivateKey(Path path) throws Exception {
		byte[] bytes = Files.readAllBytes(path);
		return decodePrivateKey(bytes);
	}
	
	public static final PrivateKey decodePrivateKey(byte[] bytes) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
		PrivateKey key = kf.generatePrivate(spec);
		return key;
	}
	
	public static PublicKey readPublicKey(Path path) throws Exception {
		byte[] bytes = Files.readAllBytes(path);
		return decodePublicKey(bytes);
	}
	
	public static final PublicKey decodePublicKey(byte[] bytes) throws Exception {
		KeyFactory kf = KeyFactory.getInstance("RSA");
		X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
		PublicKey key = kf.generatePublic(spec);
		return key;
	}
	
	public static KeyPair readKeyPair(Path publicKeyPath, Path privateKeyPath) throws Exception {
		PublicKey pub = readPublicKey(publicKeyPath);
		PrivateKey priv = readPrivateKey(privateKeyPath);
		return new KeyPair(pub, priv);
	}
	
	public static final Cipher generateEncCipher(SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Cipher generateDecCipher(SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Cipher generateCipher(int mode, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(mode, secretKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Cipher generateCipher(PrivateKey privateKey) {
		Objects.requireNonNull(privateKey, "Cannot generate Cipher from a null PrivateKey!");
		try {
			Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Cipher generateCipher(PublicKey publicKey) {
		Objects.requireNonNull(publicKey, "Cannot generate Cipher from a null PublicKey!");
		try {
			Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String[] hexChars = new String[16];
	
	protected static final String SECRET_KEY_ALGORITHM = "AES";
	protected static final int SECRET_KEY_SIZE = 256;
	
	static {
		for(int i=0; i<10; ++i) {
			hexChars[i] = String.valueOf(i);
		}
		hexChars[10] = "a";
		hexChars[11] = "b";
		hexChars[12] = "c";
		hexChars[13] = "d";
		hexChars[14] = "e";
		hexChars[15] = "f";
		try {
			secretKeyGenerator = KeyGenerator.getInstance(SECRET_KEY_ALGORITHM);
			secretKeyGenerator.init(SECRET_KEY_SIZE);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("JVM does not support " + SECRET_KEY_ALGORITHM);
		}
	}
	
	public static final <E extends Exception> IOException wrapIO(String message, E e) {
		return new IOException(message, e);
	}
	
	public static final <E extends Exception> IOException wrapIO(E e) {
		if(e instanceof IOException) {
			return (IOException) e;
		}
		return new IOException(e);
	}
	
	public static final void encryptSame(ByteBuf buf, PublicKey key) throws IllegalBlockSizeException, BadPaddingException {
		encryptSame(buf, EncodingUtils.generateCipher(key));
	}
	
	public static final void encryptSame(ByteBuf buf, HasCryptographicCredentials c) throws IllegalBlockSizeException, BadPaddingException {
		encryptSame(buf, c.getCryptoCreds().encrypt());
	}
	
	public static final void encryptSame(final ByteBuf buf, final Cipher c) throws IllegalBlockSizeException, BadPaddingException {
		encryptSame(buf, buf.readableBytes(), c);
	}
	
	public static final void encryptSame(final ByteBuf buf, final int length, final Cipher c) throws IllegalBlockSizeException, BadPaddingException {
		final int initialRead = buf.readerIndex();
		final byte[] rawBytes = new byte[length];
		buf.readBytes(rawBytes);
		final byte[] encBytes = encrypt(rawBytes, c);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		buf.writeBytes(encBytes);
		
	}
	
	public static final void encryptOther(ByteBuf plain, ByteBuf enc, Cipher c) throws IllegalBlockSizeException, BadPaddingException {
		int length = plain.readableBytes();
		byte[] rawBytes = new byte[length];
		plain.readBytes(rawBytes);
		byte[] encBytes = encrypt(rawBytes, c);
		enc.writeBytes(encBytes);
	}
	
	public static byte[] encrypt(byte[] plain, Cipher c) throws IllegalBlockSizeException, BadPaddingException {
		return c.doFinal(plain);
	}
	
	public static final Cipher decryptCipher(HasCryptographicCredentials c) {
		return c.getCryptoCreds().decrypt();
	}
	
	public static final Cipher encryptCipher(HasCryptographicCredentials c) {
		return c.getCryptoCreds().encrypt();
	}
	
	public static byte[] encrypt(byte[] bytes, HasCryptographicCredentials c) {
		try {
			return encryptCipher(c).doFinal(bytes);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Computes the SHA-256 hash of the readable bytes in the passed buf
	 * and overwrites the resulting hash in the buffer at it's initial read index.
	 * @param buf buffer to overwrite with it's current readable bytes hash.
	 */
	public static ByteBuf sha256(ByteBuf buf) {
		return digestSame(buf, "SHA-256");
	}
	
	public static byte[] sha256(byte[] bytes) {
		return digest(bytes, "SHA-256");
	}
	
	/**
	 * Computes the SHA-256 hash for all readable bytes in a {@link ByteBuffer} without
	 * modifying the buffers read/write indexes.
	 * @param buf the ByteBuffer
	 * @return SHA-256 of buffer as a hexadecimal String
	 */
	public static String toSHA256(ByteBuf buf) {
		ByteBuf tempBuf  = tempBuf();
		int wrote = EncodingUtils.digestNoRead(buf, buf.readableBytes(), tempBuf, "SHA-256");
		return ByteBufUtil.hexDump(tempBuf);
	}
	
	public static ByteBuf sha512(ByteBuf buf) {
		return encodeSame(EncodingUtils::sha512, buf);
	}
	
	public static byte[] sha512(byte[] bytes) {
		return digest(bytes, "SHA-512");
	}
	
	public static String toSHA512(ByteBuf buf) {
		ByteBuf tempBuf  = tempBuf();
		int wrote = EncodingUtils.digestNoRead(buf, buf.readableBytes(), tempBuf, "SHA-512");
		return ByteBufUtil.hexDump(tempBuf);
	}
	
	public static ByteBuf digestSame(ByteBuf buf, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			int initialRead = buf.readerIndex();
			int bytesLeft = buf.readableBytes();
			byte[] arr = tempArray();
			
			while(bytesLeft > 0) {
				int bufSize = Math.min(arr.length, bytesLeft);
				buf.readBytes(arr, 0, bufSize);
				md.update(arr, 0, bufSize);
				bytesLeft -= bufSize;
			}
			
			buf.readerIndex(initialRead);
			buf.writerIndex(initialRead);
			
			int outputSize = md.digest(arr, 0, arr.length);
			return buf.writeBytes(arr, 0, outputSize);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("System does not support " + algorithm + "!", e);
		} catch (DigestException e) {
			throw new RuntimeException("Exception thrown while digesting.", e);
		}
	}
	
	public static int digestNoRead(ByteBuf in, int length, ByteBuf out, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			int index = in.readerIndex();
			int end = index + length;
			byte[] arr = tempArray();
			
			while(index < end) {
				int bufSize = Math.min(arr.length, end - index);
				in.getBytes(index, arr, 0, bufSize);
				md.update(arr, 0, bufSize);
				index += bufSize;
			}
			
			int wrote = md.digest(arr, 0, arr.length);
			out.writeBytes(arr, 0, wrote);
			return wrote;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DigestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public static int digest(ByteBuf in, int inOffset, int inLength,
			ByteBuf out, int outOffset, int outLength,
			String algorithm) {
		
		int end = inOffset + inLength;
		if(end > in.writerIndex()) {
			throw new ArrayIndexOutOfBoundsException(String.format("End index %d is outside of input ByteBuf range [%d,%d)",
					end, inOffset, in.writerIndex()));
		}
		
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			int index = inOffset;
			byte[] arr = tempArray();
			
			while(index < end) {
				int bufSize = Math.min(arr.length, end - index);
				in.getBytes(index, arr, 0, bufSize);
				md.update(arr, 0, bufSize);
				index += bufSize;
			}
			
			index = outOffset;
			end = out.capacity();
			int wrote = md.digest(arr, 0, outLength);
			out.setBytes(outOffset, arr, 0, wrote);
			return wrote;
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DigestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return -1;
		
	}
	
	private static ByteBuffer digest(ByteBuffer in, ByteBuffer out, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			md.update(in);
			
			byte[] tempArray = tempArray();
			int wrote = md.digest(tempArray, 0, tempArray.length);
			
			return out.put(tempArray, 0, wrote);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(String.format("Current JVM does not support %s!", algorithm), e);
		} catch (DigestException e) {
			throw new RuntimeException("Exception thrown while digesting.", e);
		}
	}
	
	private static ByteBuf digest(ByteBuf in, ByteBuf out, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			int bytesLeft = in.readableBytes();
			byte[] arr = tempArray();
			
			while(bytesLeft > 0) {
				int bufSize = Math.min(arr.length, bytesLeft);
				in.readBytes(arr, 0, bufSize);
				md.update(arr, 0, bufSize);
				bytesLeft -= bufSize;
			}
			
			int outputSize = md.digest(arr, 0, arr.length);
			return out.writeBytes(arr, 0, outputSize);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError(String.format("Current JVM does not support %s!", algorithm), e);
		} catch (DigestException e) {
			throw new RuntimeException("Exception thrown while digesting.", e);
		}
	}
	
	private static byte[] digest(byte[] bytes, String algorithm) {
		try {
			MessageDigest md = MessageDigest.getInstance(algorithm);
			byte[] output = md.digest(bytes);
			return output;
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("System does not support " + algorithm + "!", e);
		}
	}
	
	@FunctionalInterface
	public static interface ByteEncoder extends Function<byte[],byte[]> {
		
	}
	
	public static byte[] extractReadableBytes(ByteBuf buf) {
		int initialRead = buf.readerIndex();
		byte[] bytes = new byte[buf.readableBytes()];
		buf.readBytes(bytes);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		return bytes;
	}
	
	public static byte[] extract(ByteBuf buf) {
		return extractReadableBytes(buf);
	}
	
	public static byte[] readAll(ByteBuf buf) {
		return read(buf, buf.readableBytes());
	}
	
	public static byte[] read(ByteBuf buf, int length) {
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		return bytes;
	}
	
	public static <T> BiConsumer<T,ByteBuf> toBufEncoder(Function<T,byte[]> encoder) {
		return (t,buf) -> buf.writeBytes(encoder.apply(t));
	}
	
	public static Consumer<ByteBuf> toBufReplace(Function<byte[],byte[]> encoder) {
		return (buf) -> encodeSame(encoder, buf);
	}
	
	public static ByteBuf encodeSame(Function<byte[],byte[]> encoder, ByteBuf buf) {
		byte[] in = extract(buf);
		return buf.writeBytes(encoder.apply(in));
	}
	
	public static ByteBuf encodeTo(Function<byte[],byte[]> encoder, ByteBuf in, ByteBuf out) {
		return out.writeBytes(encoder.apply(readAll(in)));
	}
	
	public static ByteBuf encodeToBuf(BiConsumer<ByteBuf,ByteBuf> encoder, ByteBuf in, ByteBuf out) {
		encoder.accept(in, out);
		return out;
	}
	
	private static final int SEGMENT_BITS = 0x7f;
	private static final int CONTINUE_BIT = 0x80;
	
	public static final ByteBuf writeVarShort(short value, final ByteBuf buf) {
		while((value & ~SEGMENT_BITS) != 0) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
		}
		return buf.writeByte(value);
	}
	
	public static final ByteBuf writeVarShort(final int value, final ByteBuf buf) {
		return writeVarShort((short) value, buf);
	}
	
	public static final short readVarShort(final ByteBuf buf) {
		short value = 0;
		short size = 0;
		byte b;
		while(((b = buf.readByte()) & CONTINUE_BIT) == CONTINUE_BIT) {
			value |= (b & SEGMENT_BITS) << (size++ * 7);
			if(size > 5) {
				throw new RuntimeException("VarInt too long (length must be <= 5)");
			}
		}
		return (short) (value | ((b & SEGMENT_BITS) << (size * 7)));
	}
	
	public static final ByteBuf writeVarInt(final int i, final ByteBuf buf) {
		return writeSignedVarInt(i, buf);
	}
	
	
	public static final ByteBuf writeSignedVarInt(int value, final ByteBuf buf) {
		return writeSignedVarIntProto(value, buf);
	}
	
	public static final ByteBuf writeSignedVarIntLoop(int value, final ByteBuf buf) {
		int remaining = value >> 7;
		boolean hasMore = true;
		int end = ((value & Integer.MIN_VALUE) == 0) ? 0 : -1;
		while (hasMore) {
			hasMore = (remaining != end)
					|| ((remaining & 1) != ((value >> 6) & 1));
			
			buf.writeByte((byte) ((value & SEGMENT_BITS) | (hasMore ? CONTINUE_BIT : 0)));
			value = remaining;
			remaining >>= 7;
		}
		
		return buf;
	}
	
	public static final int writeSignedVarIntCount(final int value, final ByteBuf buf) {
		final int startIndex = buf.writerIndex();
		writeSignedVarInt(value, buf);
		return buf.writerIndex() - startIndex;
	}
	
	public static final ByteBuf writeSignedVarIntUnwrapped(int value, final ByteBuf buf) {
		return writePosVarIntUnwrapped((value << 1) ^ (value >> 31), buf);
	}
	
	@Deprecated
	public static final ByteBuf writeSignedVarIntUnwrappedOld(int value, final ByteBuf buf) {
		if (((value & ~SEGMENT_BITS) != 0)) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
			if (((value & ~SEGMENT_BITS) != 0)) {
				buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
				value >>>= 7;
				if (((value & ~SEGMENT_BITS) != 0)) {
					buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
					value >>>= 7;
					if (((value & ~SEGMENT_BITS) != 0)) {
						buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
						value >>>= 7;
						return buf;
					}
					return buf;
				} else {
					return buf.writeByte(value >>= 1);
				}
			} else {
				return buf.writeByte(value >>= 1);
			}
		} else {
			return buf.writeByte(value >>= 1);
		}
		//return buf.writeByte(value >>= 1);
	}
	
	public static int readSignedVarIntUnwrapped(final ByteBuf buf) {
		final int raw = readPosVarIntUnwrapped(buf);
        // This undoes the trick in writeSignedVarInt()
        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1 << 31));
	}
	
	@Deprecated
	public static int readSignedVarIntUnwrappedOld2(final ByteBuf buf) {
		long tmp;
	    if ( (tmp = buf.readByte()) == CONTINUE_BIT) {
	      return (int) tmp;
	    }
	    long result = tmp & 0x7f;
	    int signBits = -1 << 7;;
	    if ((tmp = buf.readByte()) >= 0) {
	      result |= tmp << 7;
	      signBits <<= 7;
	    } else {
	      result |= (tmp & 0x7f) << 7;
	      signBits <<= 7;
	      if ((tmp = buf.readByte()) >= 0) {
	        result |= tmp << 14;
	        signBits <<= 7;
	      } else {
	        result |= (tmp & 0x7f) << 14;
	        signBits <<= 7;
	        if ((tmp = buf.readByte()) >= 0) {
	          result |= tmp << 21;
	          signBits <<= 7;
	        } else {
	          result |= (tmp & 0x7f) << 28;
	          //result |= (tmp & 0x7f) << 21;
	          //result |= (tmp = buf.readByte()) << 28;
	          signBits <<= 14;
	        }
	      }
	    }
//	    if (((signBits >> 1) & result) <= 0) {
//			result |= signBits;
//		}
	    return (int) result;
	}
	
	public static final ByteBuf writePosVarInt(final int value, final ByteBuf buf) {
		return writePosVarIntUnwrapped(value, buf);
	}
	
	public static final ByteBuf writePosVarIntUnwrapped(int value, final ByteBuf buf) {
		if (((value & ~SEGMENT_BITS) != 0)) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
			if (((value & ~SEGMENT_BITS) != 0)) {
				buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
				value >>>= 7;
				if (((value & ~SEGMENT_BITS) != 0)) {
					buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
					value >>>= 7;
					if (((value & ~SEGMENT_BITS) != 0)) {
						buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
						value >>>= 7;
					}
				}
			}
		}
		return buf.writeByte(value);
	}
	
	public static final ByteBuf writePosVarIntLoop(int value, final ByteBuf buf) {
		while((value & ~SEGMENT_BITS) != 0) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
		}
		return buf.writeByte(value);
	}
	
	public static final int writePosVarIntCount(final int value, final ByteBuf buf) {
		final int startIndex = buf.writerIndex();
		writePosVarInt(value, buf);
		return buf.writerIndex() - startIndex;
	}
	
	public static final int readVarInt(final ByteBuf buf) {
		return readSignedVarIntProto(buf);
	}
	
	public static final int readPosVarInt(final ByteBuf buf) {
		return EncodingUtils.readPosVarIntUnwrapped(buf);
	}
	
	public static final int readPosVarIntLoop(final ByteBuf buf) {
		int value = 0;
		int size = 0;
		int b;
		while(((b = buf.readByte()) & CONTINUE_BIT) == CONTINUE_BIT) {
			value |= (b & SEGMENT_BITS) << (size++ * 7);
			if(size > 5) {
				throw new RuntimeException("VarInt too long (length must be <= 5)");
			}
		}
		return value | ((b & SEGMENT_BITS) << (size * 7));
	}
	
	public static final int readPosVarIntUnwrapped(final ByteBuf buf) {
		int tmp;
	    if ((tmp = buf.readByte()) >= 0) {
	      return tmp;
	    }
	    int result = tmp & 0x7f;
	    if ((tmp = buf.readByte()) >= 0) {
	      result |= tmp << 7;
	    } else {
	      result |= (tmp & 0x7f) << 7;
	      if ((tmp = buf.readByte()) >= 0) {
	        result |= tmp << 14;
	      } else {
	        result |= (tmp & 0x7f) << 14;
	        if ((tmp = buf.readByte()) >= 0) {
	          result |= tmp << 21;
	        } else {
	          result |= (tmp & 0x7f) << 21;
	          result |= (tmp = buf.readByte()) << 28;
	        }
	      }
	    }
	    return result;
	}
	
	@Deprecated
	public static int readSignedVarIntUnwrappedOld(final ByteBuf buf) {
		int tmp;
	    if ( (tmp = (buf.readByte())) == CONTINUE_BIT) {
	      return tmp;
	    }
	    int result = tmp & 0x7f;
	    int signBits = -1 << 7;;
	    if ((tmp = buf.readByte()) >= 0) {
	      result |= tmp << 7;
	      signBits <<= 7;
	    } else {
	      result |= (tmp & 0x7f) << 7;
	      signBits <<= 7;
	      if ((tmp = buf.readByte()) >= 0) {
	        result |= tmp << 14;
	        signBits <<= 7;
	      } else {
	        result |= (tmp & 0x7f) << 14;
	        signBits <<= 7;
	        if ((tmp = buf.readByte()) >= 0) {
	          result |= tmp << 21;
	          signBits <<= 7;
	        } else {
	          result |= (tmp & 0x7f) << 21;
	          result |= (tmp = buf.readByte()) << 28;
	          signBits <<= 14;
	        }
	      }
	    }
//	    if (((signBits >> 1) & result) <= 0) {
//			result |= signBits;
//		}
	    return result;
	}
	
	public static int readSignedVarInt(final ByteBuf buf) {
		return readSignedVarIntProto(buf);
	}
	
	public static int readSignedVarIntLoop(final ByteBuf buf) {
		int result = 0;
		int cur;
		int count = 0;
		int signBits = -1;
		
		do {
			cur = buf.readByte();
			result |= (cur & SEGMENT_BITS) << (count * 7);
			signBits <<= 7;
			++count;
		} while (((cur & CONTINUE_BIT) == CONTINUE_BIT) && count < 5);
		
//		if ((cur & CONTINUE_BIT) == CONTINUE_BIT) {
//			throw new RuntimeException("invalid LEB128 sequence");
//		}
		
		// Sign extend if appropriate
		if (((signBits >> 1) & result) != 0) {
			result |= signBits;
		}
		
		return result;
	}
	
	public static int readSignedVarIntProto(final ByteBuf buf) {
        final int raw = readUnsignedVarIntProto(buf);
        // This undoes the trick in writeSignedVarInt()
        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1 << 31));
    }
	
	public static int readSignedVarIntProtoLoop(final ByteBuf buf) {
        final int raw = readUnsignedVarIntProtoLoop(buf);
        // This undoes the trick in writeSignedVarInt()
        int temp = (((raw << 31) >> 31) ^ raw) >> 1;
        // This extra step lets us deal with the largest signed values by treating
        // negative results from read unsigned methods as like unsigned values.
        // Must re-flip the top bit if the original read value had it set.
        return temp ^ (raw & (1 << 31));
    }
	
	public static int readUnsignedVarIntProtoLoop(final ByteBuf buf) {
		int value = 0;
		int i = 0;
		int b;
		while (((b = buf.readByte()) & CONTINUE_BIT) != 0) {
			value |= (b & SEGMENT_BITS) << i;
			i += 7;
			if(i > 35) {
				throw new RuntimeException("Variable length quantity is too long (must be <= 35)");
			}
		}
		return value | (b << i);
	}
	
	
	
	public static final int readPosVarIntUnwrapped2(final ByteBuf buf) {
		int tmp;
	    if ((tmp = buf.readByte()) >= 0) {
	      return tmp;
	    }
	    int result = tmp & 0x7f;
	    if ((tmp = buf.readByte()) >= 0) {
	      result |= tmp << 7;
	    } else {
	      result |= (tmp & 0x7f) << 7;
	      if ((tmp = buf.readByte()) >= 0) {
	        result |= tmp << 14;
	      } else {
	        result |= (tmp & 0x7f) << 14;
	        if ((tmp = buf.readByte()) >= 0) {
	          result |= tmp << 21;
	        } else {
	          result |= (tmp & 0x7f) << 21;
	          result |= (tmp = buf.readByte()) << 28;
	        }
	      }
	    }
	    return result;
	}
	
	public static final int readPosVarIntUnwrappedGood(final ByteBuf buf) {
		int tmp;
	    if ((tmp = buf.readByte()) >= 0) {
	      return tmp;
	    }
	    int result = tmp & 0x7f;
	    if ((tmp = buf.readByte()) >= 0) {
	      result |= tmp << 7;
	    } else {
	      result |= (tmp & 0x7f) << 7;
	      if ((tmp = buf.readByte()) >= 0) {
	        result |= tmp << 14;
	      } else {
	        result |= (tmp & 0x7f) << 14;
	        if ((tmp = buf.readByte()) >= 0) {
	          result |= tmp << 21;
	        } else {
	          result |= (tmp & 0x7f) << 21;
	          result |= (tmp = buf.readByte()) << 28;
	        }
	      }
	    }
	    return result;
	}
	
	public static int readUnsignedVarIntProto(final ByteBuf buf) {
		int tmp;
		int result = 0;
		int i = 0;
		if (((tmp = buf.readByte()) & CONTINUE_BIT) != 0) {
			result |= (tmp & SEGMENT_BITS);
			i += 7;
			if (((tmp = buf.readByte()) & CONTINUE_BIT) != 0) {
				result |= (tmp & SEGMENT_BITS) << i;
				i += 7;
				if (((tmp = buf.readByte()) & CONTINUE_BIT) != 0) {
					result |= (tmp & SEGMENT_BITS) << i;
					i += 7;
					if (((tmp = buf.readByte()) & CONTINUE_BIT) != 0) {
						result |= (tmp & SEGMENT_BITS) << i;
						i += 7;
						if (((tmp = buf.readByte()) & CONTINUE_BIT) != 0) {
							result |= (tmp & SEGMENT_BITS) << i;
							i += 7;
						}
					}
				}
			}
		}
		return result | (tmp << i);
	}
	
	public static ByteBuf writeSignedVarIntProto(final int value, final ByteBuf out)  {
        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
		return writeUnsignedVarIntProto((value << 1) ^ (value >> 31), out);
    }
	
	private static final long U_VARINT_MASK = 0xFFFFFF80;
	
	public static ByteBuf writeSignedVarIntProtoLoop(final int value, final ByteBuf out)  {
        // Great trick from http://code.google.com/apis/protocolbuffers/docs/encoding.html#types
		return writeUnsignedVarIntProtoLoop((value << 1) ^ (value >> 31), out);
    }
	
	public static ByteBuf writeUnsignedVarIntProtoLoop(int value, final ByteBuf buf) {
		while ((value & U_VARINT_MASK) != 0L) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
		}
		return buf.writeByte(value & SEGMENT_BITS);
	}
	
	public static ByteBuf writeUnsignedVarIntProto(int value, final ByteBuf buf) {
		if ((value & U_VARINT_MASK) != 0L) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
			if ((value & U_VARINT_MASK) != 0L) {
				buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
				value >>>= 7;
				if ((value & U_VARINT_MASK) != 0L) {
					buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
					value >>>= 7;
					if ((value & U_VARINT_MASK) != 0L) {
						buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
						value >>>= 7;
					}
				}
			}
		}
		return buf.writeByte(value & SEGMENT_BITS);
	}
	
	private static final long SEGMENT_BITS_LONG = (long) SEGMENT_BITS;
	private static final long CONTINUE_BIT_LONG = (long) CONTINUE_BIT;
	
	public static final ByteBuf writeVarLong(long value, final ByteBuf buf) {
		while((value & ~(SEGMENT_BITS_LONG)) != 0) {
			buf.writeByte((int) ((value & SEGMENT_BITS_LONG) | CONTINUE_BIT_LONG));
			value >>>= 7;
		}
		return buf.writeByte((int) value);
	}
	
	public static final long readVarLong(final ByteBuf buf) {
		return readVarLongUnwrapped(buf);
	}
	
	public static final long readVarLongLoop(final ByteBuf buf) {
		long value = 0;
		int position = 0;
		long currentByte;
		while(true) {
			currentByte = buf.readByte();
			value |= (currentByte & SEGMENT_BITS) << position;
			if ((currentByte & CONTINUE_BIT) == 0) break;
			position += 7;
			if (position >= 64) throw new RuntimeException("VarLong is too big");
		}
		return value;
	}
	
	public static final long readVarLongUnwrapped(final ByteBuf buf) {
		
		long tmp;
		if ((tmp = buf.readByte()) >= 0) {
			return tmp;
		}
		long result = tmp & 0x7f;
		if ((tmp = buf.readByte()) >= 0) {
			result |= tmp << 7;
		} else {
			result |= (tmp & 0x7f) << 7;
			if ((tmp = buf.readByte()) >= 0) {
				result |= tmp << 14;
			} else {
				result |= (tmp & 0x7f) << 14;
				if ((tmp = buf.readByte()) >= 0) {
					result |= tmp << 21;
				} else {
					result |= (tmp & 0x7f) << 21;
					if ((tmp = buf.readByte()) >= 0) {
						result |= tmp << 28;
					} else {
						result |= (tmp & 0x7f) << 28;
						if ((tmp = buf.readByte()) >= 0) {
							result |= tmp << 35;
						} else {
							result |= (tmp & 0x7f) << 35;
							if ((tmp = buf.readByte()) >= 0) {
								result |= tmp << 42;
							} else {
								result |= (tmp & 0x7f) << 42;
								if ((tmp = buf.readByte()) >= 0) {
									result |= tmp << 49;
								} else {
									result |= (tmp & 0x7f) << 49;
									if ((tmp = buf.readByte()) >= 0) {
										result |= tmp << 56;
									} else {
										result |= (tmp & 0x7f) << 56;
										result |= ((long) buf.readByte()) << 63;
									}
								}
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public static final class VarLongProcessor implements ByteProcessor {
		private long result = 0L;
		private long temp;
		private int position = 0;
		public final boolean hasValue() {
			return position > 0;
		}
		public final long getValue() {
			return result;
		}
		public final VarLongProcessor reset() {
			result = 0L;
			position = 0;
			return this;
		}
		public final int getLength() {
			return position / 7;
		}
		@Override
		public final boolean process(final byte currentByte) throws Exception {
			temp = currentByte;
			result |= (temp & SEGMENT_BITS) << position;
			position += 7;
			if((temp & CONTINUE_BIT) == 0) {
				return false;
			}
			if (position >= 64) {
				throw new RuntimeException("VarLong is too big");
			}
			return true;
		}
	}
	
	private static final ThreadLocal<VarLongProcessor> vlp = ThreadLocal.withInitial(VarLongProcessor::new);
	
	public static final long readVarLongProcess(final ByteBuf buf) {
		VarLongProcessor p = vlp.get().reset();
		buf.readerIndex(buf.forEachByte(p) + 1);
		return p.getValue();
	}
	
	public static final ByteBuf writeUByte(final int value, final ByteBuf buf) {
		return buf.writeByte(value);
	}
	
	public static final int readUByte(final ByteBuf buf) {
		return buf.readByte() & 0xFF;
	}
	
	public static final ByteBuf writeUShort(final int value, final ByteBuf buf) {
		return buf.writeShort(value);
	}
	
	public static final int readUShort(final ByteBuf buf) {
		return buf.readShort() & 0xFFFF;
	}
	
	public static final ByteBuf writeVarArray(final byte[] arr, final ByteBuf buf) {
		return writePosVarInt(arr.length, buf)
				.writeBytes(arr);
	}
	
	public static final ByteBuf writeVarArray(final byte[] arr, final int index, final int length, final ByteBuf buf) {
		return writePosVarInt(length, buf)
				.writeBytes(arr, index, length);
	}
	
	public static final byte[] readVarArray(final ByteBuf buf) {
		final byte[] arr = new byte[readPosVarInt(buf)];
		buf.readBytes(arr);
		return arr;
	}
	
	public static final ByteBuf writeIntArrayWithLength(final int[] arr, final ByteBuf buf) {
		writePosVarInt(arr.length, buf);
		return writeIntArray(arr, buf);
	}
	
	public static final ByteBuf writeIntArray(final int[] arr, final ByteBuf buf) {
		for(int i=0; i<arr.length; ++i) {
			buf.writeInt(arr[i]);
		}
		return buf;
	}
	
	public static final int[] readVarIntArray(final ByteBuf buf) {
		return readIntArray(EncodingUtils.readPosVarInt(buf), buf);
	}
	
	public static final int[] readIntArray(final int length, final ByteBuf buf) {
		final int[] arr = new int[length];
		for(int i=0; i<length; ++i) {
			arr[i] = buf.readInt();
		}
		return arr;
	}
	
	public static final ByteBuf writeLongArray(final long[] arr, final ByteBuf buf) {
		writePosVarInt(arr.length, buf);
		for(int i=0; i<arr.length; ++i) {
			buf.writeLong(arr[i]);
		}
		return buf;
	}
	
	public static final long[] readLongArray(final ByteBuf buf) {
		int length = readPosVarInt(buf);
		long[] arr = new long[length];
		for(int i=0; i<length; ++i) {
			arr[i] = buf.readLong();
		}
		return arr;
	}
	
	/**
	 * Encodes a List of Objects that can be encoded into a ByteBuf by passing a mapping function.
	 * The list has a header consisting of size of list(int) and
	 * if the list is not empty then a signed Int  that indicates how long the rest of the encoded List is in bytes.
	 * @param list The list of objects to be encoded
	 * @param encoder The encoding function that consumes a element and writes it to a passed ByteBuf.
	 * @param buf The ByteBuf to encode the bytes into
	 * @param <M> The message type to be encoded
	 * @return
	 */
	public static final <M> ByteBuf encodeList(final List<M> list, final BiConsumer<? super M,ByteBuf> encoder, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(list.size(), buf);
		if(list.isEmpty()) {
			return buf;
		}
		
		final int byteLengthIndex = buf.writerIndex();
		buf.writeInt(0);
		
		for(M m: list) {
			encoder.accept(m, buf);
		}
		
		buf.setInt(byteLengthIndex, buf.writerIndex()-byteLengthIndex-4);
		return buf;
	}
	
	public static final <M> List<M> decodeList(final Supplier<List<M>> sup, final Function<ByteBuf,M> decoder, final ByteBuf buf) {
		final int count = EncodingUtils.readPosVarInt(buf);
		if(count < 1) return Collections.emptyList();
		
		final List<M> list = sup.get();
		final int byteLength = buf.readInt();
		
		for(int i=0; i<count; ++i) {
			list.add(decoder.apply(buf));
		}
		
		return list;
	}
	
	public static final <M> ByteBuf encodeIndexedList(final List<M> list, final BiConsumer<? super M,ByteBuf> encoder, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(list.size(), buf);
		if(list.isEmpty()) {
			return buf;
		}
		
		final int sizeIndex = buf.writerIndex();
		buf.writeInt(0);
		
		final int lookupTableIndex = buf.writerIndex();
		buf.writeBytes(new byte[4 * list.size()]);
		
		final int lookupTableRelativeIndex = buf.writerIndex();
		
		int n = 0;
		for(M m: list) {
			buf.setInt(lookupTableIndex + 4*n, buf.writerIndex() - lookupTableRelativeIndex);
			encoder.accept(m, buf);
			++n;
		}
		
		buf.setInt(sizeIndex, buf.writerIndex()-sizeIndex-4);
		return buf;
	}
	
	public static final <M> List<M> decodeIndexedList(final Supplier<List<M>> sup, final IntIntObjFunction<ByteBuf,M> decoder, final ByteBuf buf) {
		
		final int size = EncodingUtils.readPosVarInt(buf);
		if(size > 0) {
			final List<M> list = sup.get();
			final int byteLength = buf.readInt();
			final int lookupTableIndex = buf.readerIndex();
			final int lookupTableRelativeIndex = lookupTableIndex + 4*size;
			final int[] lookupTable = readIntArray(size, buf);
			for(int i=0; i<size; ++i) {
				list.add(decoder.apply(i, lookupTable[i] + lookupTableRelativeIndex, buf));
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}
	
	public static final <M> List<M> decodeIndexedList(final Supplier<List<M>> sup, final IntObjFunction<ByteBuf,M> decoder, final ByteBuf buf) {
		final int size = EncodingUtils.readPosVarInt(buf);
		if(size > 0) {
			final List<M> list = sup.get();
			final int byteLength = buf.readInt();
			final int lookupTableIndex = buf.readerIndex();
			final int lookupTableRelativeIndex = lookupTableIndex + 4*size;
			final int[] lookupTable = readIntArray(size, buf);
			for(int i=0; i<size; ++i) {
				list.add(decoder.apply(i, buf));
			}
			return list;
		} else {
			return Collections.emptyList();
		}
	}
	
	/**
	 * Encodes an array of Objects that can be encoded into a ByteBuf by passing an encoding BiConsumer.
	 * The array has a header consisting of the length of the list as a PosVarIntCount
	 * and if the list is not empty then a signed Int  that indicates how long the rest of the encoded List is in bytes,
	 * @param <M> The message type to be encoded
	 * @param buf The ByteBuf to encode the bytes into
	 * @param list The array of objects to be encoded
	 * @param encoder The encoding function that consumes a element and writes it to a passed ByteBuf.
	 * @return
	 */
	public static final <M> ByteBuf encodeObjectArray(final ByteBuf buf, M[] arr, final BiConsumer<? super M,ByteBuf> encoder) {
		EncodingUtils.writePosVarInt(arr.length, buf);
		if(arr.length < 1) {
			return buf;
		}
		
		final int sizeIndex = buf.writerIndex();
		buf.writeInt(0);
		
		for(M m: arr) {
			encoder.accept(m, buf);
		}
		
		buf.setInt(sizeIndex, buf.writerIndex()-sizeIndex);
		return buf;
	}
	
	@SuppressWarnings("unchecked")
	public static final <M> M[] decodeObjectArray(final ByteBuf buf, final Class<M> clazz, final Function<ByteBuf,M> decoder) {
		final int length = EncodingUtils.readPosVarInt(buf);
		if(length < 1) {
			return (M[]) Array.newInstance(clazz, length);
		}
		
		final int readStart = buf.readerIndex();
		final int totalBytes = buf.readInt();
		
		final M[] arr = (M[]) Array.newInstance(clazz, length);
		
		for(int i=0; i<length; ++i) {
			arr[i] = decoder.apply(buf);
		}
		
		final int readBytes = buf.readerIndex() - readStart;
		
		if(readBytes != totalBytes) {
			throw new DecoderException("Decoded more bytes than should have for Object Array!");
		}
		
		return arr;
		
	}
	
	public static final <M> ByteBuf encode2DObjectArrayNoIndex(final ByteBuf buf, final M[][] arr, final BiConsumer<? super M,ByteBuf> encoder) {
		EncodingUtils.writePosVarInt(arr.length, buf);
		EncodingUtils.writePosVarInt(arr[0].length, buf);
		
		for(M[] a : arr) {
			for(M e: a) {
				encoder.accept(e, buf);
			}
		}
		
		return buf;
	}
	
	public static final <M> M[][] decode2DObjectArrayNoIndex(final ByteBuf buf, final Class<M> clazz, final Function<ByteBuf,M> decoder) {
		final int height = EncodingUtils.readPosVarIntUnwrapped(buf);
		final int width = EncodingUtils.readPosVarIntUnwrapped(buf);
		
		if(height < 1 || width < 1) {
			return null;
		}
		
		final M[][] arr = (M[][]) Array.newInstance(clazz, height, width);
		for(int y=0; y<height; ++y) {
			for(int x=0; x<width; ++x) {
				arr[y][x] = decoder.apply(buf);
			}
		}
		
		return arr;
		
	}
	
	public static final <V> ByteBuf encodeSizedCollection(final Collection<V> c, final BiConsumer<V,ByteBuf> encoder, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(c.size(), buf);
		final int byteLengthIndex = buf.writerIndex();
		buf.writeInt(0);
		final int byteLengthCompareIndex = buf.writerIndex();
		
		for(final V e: c) {
			encoder.accept(e, buf);
		}
		
		buf.setInt(byteLengthIndex, buf.writerIndex() - byteLengthCompareIndex);
		return buf;
	}
	
	public static final <V> ByteBuf encodeCollection(final Collection<V> c, final BiConsumer<V,ByteBuf> encoder, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(c.size(), buf);
		for(final V e: c) {
			encoder.accept(e, buf);
		}
		return buf;
	}
	
	public static final <V> ByteBuf encodeSizedCollection(final Collection<V> c, final BiFunction<V,ByteBuf,ByteBuf> encoder, final ByteBuf buf) {
		return encodeSizedCollection(c, Utils.consume(encoder), buf);
	}
	
	public static final <V> ByteBuf encodeCollection(final Collection<V> c, final BiFunction<V,ByteBuf,ByteBuf> encoder, final ByteBuf buf) {
		return encodeCollection(c, Utils.consume(encoder), buf);
	}
	
	public static final <V,C extends Collection<V>> C decodeSizedCollection(final Supplier<C> sup, final Function<ByteBuf,V> decoder, final ByteBuf buf) {
		final C c = sup.get();
		final int size = EncodingUtils.readPosVarInt(buf);
		if(size < 1) {
			return c;
		}
		
		final int byteLength = buf.readInt();
		final int readStart = buf.readerIndex();
		final int byteEndIndex = byteLength + readStart;
		
		for(int i=0; i<size; ++i) {
			c.add(decoder.apply(buf));
		}
		
		if(buf.readerIndex() != byteEndIndex) {
			final int bytesRead = buf.readerIndex() - readStart;
			throw new DecoderException(String.format("decodingSizedCollection read %d bytes instead of %d from %s", bytesRead, byteLength, buf));
		}
		
		return c;
	}
	
	public static final <V,C extends Collection<V>> C decodeCollection(final Supplier<C> sup, final Function<ByteBuf,V> decoder, final ByteBuf buf) {
		final C c = sup.get();
		
		final int size = EncodingUtils.readPosVarInt(buf);
		for(int i=0; i<size; ++i) {
			c.add(decoder.apply(buf));
		}
		
		return c;
	}
	
	public static final <K,V> ByteBuf encodeMap(final Map<K,V> map, final TriConsumer<K,V,ByteBuf> encoder, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(map.size(), buf);
		for(final Entry<K,V> e: map.entrySet()) {
			encoder.accept(e.getKey(), e.getValue(), buf);
		}
		return buf;
	}
	
	public static final <K,V> ByteBuf encodeMap(final Map<K,V> map, final TriFunction<K,V,ByteBuf,ByteBuf> encoder, ByteBuf buf) {
		return encodeMap(map, Utils.consume(encoder), buf);
	}
	
	public static final <V> ByteBuf encodeIntMap(final IntObjectMap<V> map, final IntObjObjFunction<V,ByteBuf,ByteBuf> encoder, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(map.size(), buf);
		for(final PrimitiveEntry<V> e: map.entries()) {
			encoder.apply(e.key(), e.value(), buf);
		}
		return buf;
	}
	
	public static final <V> IntObjectMap<V> decodeNullableIntMap(final BiConsumer<IntObjectMap<V>,ByteBuf> f, final ByteBuf buf) {
		final int size = EncodingUtils.readPosVarIntUnwrapped(buf);
		if(size > 0) {
			final IntObjectMap<V> map = new IntObjectHashMap<>();
			for(int i=0; i<size; ++i) {
				f.accept(map, buf);
			}
			return map;
		} else {
			return null;
		}
	}
	
	public static final <V> IntObjectMap<V> decodIntMap(final BiConsumer<IntObjectMap<V>,ByteBuf> f, final ByteBuf buf) {
		final int size = EncodingUtils.readPosVarIntUnwrapped(buf);
		final IntObjectMap<V> map = new IntObjectHashMap<>();
		for(int i=0; i<size; ++i) {
			f.accept(map, buf);
		}
		return map;
	}
	
	public static final <V> ByteBuf encodeIntMap(final IntObjectMap<V> map, final IntObjObjConsumer<V,ByteBuf> f, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(map.size(), buf);
		for(final PrimitiveEntry<V> e: map.entries()) {
			f.accept(e.key(), e.value(), buf);
		}
		return buf;
	}
	
	public static final <K,V,R> ByteBuf encodeMap(final Map<K,V> map, final BiFunction<K,V,R> f, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(map.size(), buf);
		for(final Entry<K,V> e: map.entrySet()) {
			f.apply(e.getKey(), e.getValue());
		}
		return buf;
	}
	
	public static final <K,V> ByteBuf encodeMap(final Map<K,V> map, final BiConsumer<? super Entry<K,V>,ByteBuf> f, final ByteBuf buf) {
		EncodingUtils.writePosVarInt(map.size(), buf);
		for(final Entry<K,V> e: map.entrySet()) {
			f.accept(e, buf);
		}
		return buf;
	}
	
	public static ByteBuf decryptSameFast(final ByteBuf buf, final Cipher cipher) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException  {
		int initialRead = buf.readerIndex();
		int enclength = buf.readableBytes();
		byte[] arr = tempArray(enclength);
		buf.readBytes(arr, 0, enclength);
		int plainLength = cipher.doFinal(arr, 0, enclength, arr);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		return buf.writeBytes(arr, 0, plainLength);
	}
	
	public static ByteBuf decryptSameFast(final ByteBuf buf, final HasCryptographicCredentials c) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException  {
		return decryptSameFast(buf, decryptCipher(c));
	}
	
	public static ByteBuf decryptSame(final ByteBuf buf, final HasCryptographicCredentials c) throws IllegalBlockSizeException, BadPaddingException  {
		int initialRead = buf.readerIndex();
		byte[] enc = new byte[buf.readableBytes()];
		buf.readBytes(enc);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		byte[] plain = decryptCipher(c).doFinal(enc);
		return buf.writeBytes(plain);
	}
	
	public static ByteBuf decryptSame(final ByteBuf buf, final Cipher cipher) throws IllegalBlockSizeException, BadPaddingException  {
		return decryptSame(buf, buf.readableBytes(), cipher);
	}
	
	public static ByteBuf decryptSame(final ByteBuf buf, final int length, final Cipher decCipher) throws IllegalBlockSizeException, BadPaddingException  {
		final int initialRead = buf.readerIndex();
		final byte[] encBytes = new byte[length];
		buf.readBytes(encBytes);
		final byte[] plain = decCipher.doFinal(encBytes);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		return buf.writeBytes(plain);
	}
	
	public static ByteBuf decryptSameByteArrayBuf(final ByteBuf buf, final int length, final Cipher decCipher) throws IllegalBlockSizeException, BadPaddingException  {
		final int initialRead = buf.readerIndex();
		final byte[] bytes = EncodingUtils.tempArray(length);
		buf.readBytes(bytes, 0, length);
		try {
			final int plainLength = decCipher.doFinal(bytes, 0, length, bytes, 0);
			buf.readerIndex(initialRead);
			buf.writerIndex(initialRead);
			return buf.writeBytes(bytes, 0, plainLength);
		} catch (ShortBufferException | IllegalBlockSizeException | BadPaddingException e) {
			e.printStackTrace();
			throw new DecoderException("Failed to decrypyt byte array into same array!");
		}
	}
	
	public static ByteBuf decryptOther(final ByteBuf plain, final ByteBuf enc, final Cipher decCipher) throws IllegalBlockSizeException, BadPaddingException {
		plain.writeBytes(enc);
		return decryptSame(plain, decCipher);
	}
	
	public static byte[] decrypt(byte[] bytes, HasCryptographicCredentials c) throws IllegalBlockSizeException, BadPaddingException {
		return decryptCipher(c).doFinal(bytes);
	}
	
	public static byte[] decrypt(byte[] bytes, SecretKey secretKey) {
		try {
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			return cipher.doFinal(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] decrypt(byte[] bytes, PrivateKey privateKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher.doFinal(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] decrypt(byte[] in, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException {
		return cipher.doFinal(in);
	}
	
	public static final ByteBuf writeVarSizeArray(ByteBuf buf, byte[] bytes) {
		return buf.writeBytes(bytes);
	}
	
	public static final ByteBuf writeIntSizedArray(ByteBuf buf, byte[] arr) {
		return buf.writeInt(arr.length)
				.writeBytes(arr);
	}
	
	public static final byte[] readIntSizedArray(ByteBuf buf) {
		int length = buf.readInt();
		byte[] bytes = new byte[length];
		buf.readBytes(bytes);
		return bytes;
	}
	
	public static final String DEFAULT_STRING_ENCODING = "UTF-8";
	public static final Charset DEFAULT_CHARSET = Charset.forName(DEFAULT_STRING_ENCODING);
	
	public static String readIntString(final ByteBuf buf, final Charset charset) {
		final int length = buf.readInt();
		final int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		final String text = buf.toString(readIndex, length, charset);
		return text;
	}
	
	public static String readIntString(final ByteBuf buf) {
		return readIntString(buf, DEFAULT_CHARSET);
	}
	
	public static String readByteString(final ByteBuf buf, final Charset charset) {
		final int length = readUByte(buf);
		final int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		final String text = buf.toString(readIndex, length, charset);
		return text;
	}
	
	public static String readByteString(final ByteBuf buf) {
		return readByteString(buf, DEFAULT_CHARSET);
	}
	
	public static String readVarIntString(final ByteBuf buf, final Charset charset) {
		final int length = readPosVarInt(buf);
		final int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		return buf.toString(readIndex, length, charset);
	}
	
	public static String readVarString(final ByteBuf buf) {
		return readVarIntString(buf, DEFAULT_CHARSET);
	}
	
	public static String readString(ByteBuf buf) {
		return readVarIntString(buf, DEFAULT_CHARSET);
	}
	
	public static final ByteBuf writeString(final String s, final ByteBuf buf) {
		return writeVarString(s, buf);
	}
	
	public static final ByteBuf writeVarString(final String s, final ByteBuf buf) {
		return writeVarIntString(s, DEFAULT_CHARSET, buf);
	}
	
	public static final ByteBuf writeNullableVarString(final String s, final ByteBuf buf) {
		if(s == null) {
			return writePosVarInt(0, buf);
		}
		return writeVarIntString(s, DEFAULT_CHARSET, buf);
	}
	
	public static final String readNullablerVarString(final ByteBuf buf) {
		return readNullablerVarString(buf, DEFAULT_CHARSET);
	}
	
	public static final String readNullablerVarString(final ByteBuf buf, final Charset charset) {
		final int length = readPosVarInt(buf);
		if(length == 0) {
			return null;
		}
		final int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		return buf.toString(readIndex, length, charset);
	}
	
	public static final ByteBuf writeVarIntString(final String s, final Charset charset, final ByteBuf buf) {
		final byte[] arr = s.getBytes(charset);
		return writeVarArray(arr, buf);
	}
	
	public static final ByteBuf writeByteString(final String s, final Charset charset, final ByteBuf buf) {
		final byte[] arr = s.getBytes(charset);
		writeUByte(arr.length, buf);
		return buf.writeBytes(arr);
	}
	
	public static final ByteBuf writeByteString(final String s, final ByteBuf buf) {
		return writeByteString(s, DEFAULT_CHARSET, buf);
	}
	
	public static final void writeShortString(final String s, final ByteBuf buf) {
		final byte[] arr = s.getBytes();
		buf.writeShort(arr.length);
		buf.writeBytes(arr);
	}
	
	public static final void writeIntString(final String s, final Charset charset, final ByteBuf buf) {
		final byte[] arr = s.getBytes(charset);
		buf.writeInt(arr.length);
		buf.writeBytes(arr);
	}
	
	public static final void writeIntString(final String s, final ByteBuf buf) {
		writeIntString(s, DEFAULT_CHARSET, buf);
	}
	
	public static final void encode(final PublicKey key, final ByteBuf buf) {
		buf.writeBytes(key.getEncoded());
	}
	
	public static final PublicKey decodeRSAPublicKey(final ByteBuf buf) throws InvalidKeySpecException, NoSuchAlgorithmException {
		final byte[] arr = new byte[buf.readableBytes()];
		buf.readBytes(arr);
		return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(arr));
	}
	
	public static final SecretKey decodeAESKey(ByteBuf buf) {
		int length = buf.readableBytes();
		byte[] bytes = tempArray();
		buf.readBytes(bytes, 0, length);
		return new SecretKeySpec(bytes, 0, length, "AES");
	}
	
//	public static int compress(Deflater deflater, ByteBuffer in, ByteBuffer out) {
//		try {
//			deflater.reset();
//			deflater.setInput(in.ar);
//			deflater.finish();
//			return deflater.deflate(out);
//		} finally {
//			deflater.end();
//		}
//	}
	
//	public static int compress(ByteBuffer in, ByteBuffer out) {
//		return compress(new Deflater(Deflater.DEFAULT_COMPRESSION), in, out);
//	}
	
	public static final byte[] compress(final byte[] data) {
		Deflater dfl = new Deflater(Deflater.DEFAULT_COMPRESSION);
		dfl.setInput(data);
		dfl.finish();
		
		byte[] buffer = tempArray();
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			while (!dfl.finished()) {
				int n = dfl.deflate(buffer);
				baos.write(buffer, 0, n);
			}
			return baos.toByteArray();
		} catch (Throwable t) {
			t.printStackTrace();
			return null;
		} finally {
			dfl.end();
		}
	}
	
	public static final ByteBuf compressSameAsArray(final ByteBuf buf) {
		return EncodingUtils.encodeSame(EncodingUtils::compress, buf);
	}
	
	//JDK 11 dependent
//	public static int decompress(Inflater inflater, ByteBuffer in, ByteBuffer out) throws DataFormatException {
//		//inflater.reset();
//		//buffer.position(buffer.capacity());
//		inflater.setInput(in);
//		return inflater.inflate(out);
//	}
//	
//	public static int decompress(ByteBuffer in, ByteBuffer out) throws DataFormatException {
//		Inflater inflater = new Inflater();
//		try {
//			return decompress(inflater, in, out);
//		} finally {
//			inflater.end();
//		}
//	}
//	
//	public static final ByteBuf decompressSameAsBuffer(ByteBuf buf) throws DataFormatException {
//		ByteBuffer inBuffer = buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes());
//		ByteBuffer tempBuffer = tempBuffer();
//		int initialRead = buf.readerIndex();
//		int wrote = decompress(inBuffer, tempBuffer);
//		tempBuffer.flip();
//		buf.readerIndex(initialRead);
//		buf.writerIndex(initialRead);
//		buf.writeBytes(tempBuffer);
//		return buf;
//	}
//	public static final ByteBuf compressSameAsBuffer(ByteBuf buf) {
//	ByteBuffer inBuffer = buf.internalNioBuffer(buf.readerIndex(), buf.readableBytes());
//	ByteBuffer tempBuffer = tempBuffer();
//	int initialRead = buf.readerIndex();
//	int wrote = compress(inBuffer, tempBuffer);
//	tempBuffer.flip();
//	buf.readerIndex(initialRead);
//	buf.writerIndex(initialRead);
//	buf.writeBytes(tempBuffer);
//	return buf;
//}
	
	public static final byte[] decompress(final byte[] compressedBytes) {
		Inflater infl = new Inflater();
		infl.setInput(compressedBytes, 0, compressedBytes.length);
		
		byte[] buffer = tempArray();
		try(ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
			while (!infl.finished()) {
				int n = infl.inflate(buffer);
				baos.write(buffer, 0, n);
			}
			return baos.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		} finally {
			infl.end();
		}
	}
	
	public static final ByteBuf decompressSame(final ByteBuf buf) {
		return EncodingUtils.encodeSame(EncodingUtils::decompress, buf);
	}
	
	public static final ByteBuf decompressNew(final ByteBuf in, final ByteBuf out) {
		return EncodingUtils.encodeTo(EncodingUtils::decompress, in, out);
	}
	
	public static final ByteBuf decompressNew(final Channel channel, final ByteBuf in) {
		return EncodingUtils.encodeTo(EncodingUtils::decompress, in, channel.alloc().buffer(in.readableBytes()));
	}
	
	public static String toBinaryString(final byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}
	
	public static String toBinaryString(final int i) {
		return String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0');
	}
	
	
	
	private static String getSingleHexChar(final int i) {
		return hexChars[i];
	}
	
	public static void newPretty(final StringBuilder builder, final byte b) {
		for(int i=7; i>=0; --i) {
			int bit = (b >> i) & 1;
			builder.append(bit);
		}
	}
	
	public static void newPretty(final StringBuilder b, final byte[] bytes) {
		b.append("[");
		if(bytes.length > 0)
			b.append("\n");
		for(int i=0; i<bytes.length; ++i)
			newPretty(b, bytes[i]);
		b.append("\n]");
	}
	
	public static void pretty(final StringBuilder b, final byte[] bytes) {
		b.append("[");
		if(bytes.length > 0)
			b.append("\n");
		for(int i=0; i<bytes.length; ++i)
			b.append(toBinaryString(bytes[i])).append("\n");
		b.append("]");
	}
	
	public static String pretty(final byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		pretty(builder, bytes);
		return builder.toString();
	}
	
	public static String getPrettyHexTableIndex(final int index) {
		int row = index / 16;
		int column = index % 16;
		return toBinaryString(row) + ":" + getSingleHexChar(column);
	}
	
}
