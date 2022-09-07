package com.thora.core.net.netty;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.DigestException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
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

import com.thora.core.net.HasCryptographicCredentials;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.util.ByteProcessor;

public class EncodingUtils {
	
	public static final Pattern REGEX_SOCKET_ADDRESS = Pattern.compile("^\\s*(?<host>.*)\\:(?<port>\\d+)\\s*$");
	
	public static final int TEMP_BYTE_BUFFER_SIZE = 10 * 1024;
	
	private static ThreadLocal<ByteBuffer> tempByteBuffer = ThreadLocal.withInitial(() -> ByteBuffer.allocateDirect(TEMP_BYTE_BUFFER_SIZE));
	private static ThreadLocal<ByteBuf> tempByteBuf = ThreadLocal.withInitial(() -> Unpooled.directBuffer());
	
	public static final int TEMP_BYTE_ARRAY_SIZE = 4 * 1024;
	public static ThreadLocal<byte[]> bufferByteArray = ThreadLocal.withInitial(() -> new byte[TEMP_BYTE_ARRAY_SIZE]);
	
	private static ByteBuffer tempBuffer() {
		ByteBuffer buffer = tempByteBuffer.get();
		buffer.clear();
		return buffer;
	}
	
	private static ByteBuf tempBuf() {
		return tempByteBuf.get().clear();
	}
	
	private static byte[] tempArray() {
		return bufferByteArray.get();
	}
	
	private static byte[] tempArray(int minSize) {
		if(minSize > TEMP_BYTE_ARRAY_SIZE)
			return new byte[minSize];
		return tempArray();
	}
	
	public static final InetSocketAddress parseSocketAddress(String text) {
		Matcher m = REGEX_SOCKET_ADDRESS.matcher(text);
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
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static final Cipher generateCipher(PublicKey publicKey) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			return cipher;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static final String[] hexChars = new String[16];
	
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
			secretKeyGenerator = KeyGenerator.getInstance("AES");
			secretKeyGenerator.init(256);
		} catch (NoSuchAlgorithmException e) {
			throw new AssertionError("JVM does not support AES!");
		}
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
	
	public static final void encryptSame(ByteBuf buf, Cipher c) throws IllegalBlockSizeException, BadPaddingException {
		encryptSame(buf, buf.readableBytes(), c);
	}
	
	public static final void encryptSame(ByteBuf buf, int length, Cipher c) throws IllegalBlockSizeException, BadPaddingException {
		int initialRead = buf.readerIndex();
		byte[] rawBytes = new byte[length];
		buf.readBytes(rawBytes);
		byte[] encBytes = encrypt(rawBytes, c);
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
	
	private static ByteBuf digestSame(ByteBuf buf, String algorithm) {
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
	
	public static final ByteBuf writeVarShort(short value, ByteBuf buf) {
		while((value & ~SEGMENT_BITS) != 0) {
			buf.writeByte((value & SEGMENT_BITS) | CONTINUE_BIT);
			value >>>= 7;
		}
		return buf.writeByte(value);
	}
	
	public static final ByteBuf writeVarShort(int value, ByteBuf buf) {
		return writeVarShort((short) value, buf);
	}
	
	public static final short readVarShort(ByteBuf buf) {
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
	
	public static final ByteBuf writeVarInt(int i, ByteBuf buf) {
		return writeSignedVarInt(i, buf);
	}
	
	
	public static final ByteBuf writeSignedVarInt(int value, ByteBuf buf) {
		int remaining = value >> 7;
		boolean hasMore = true;
		int end = ((value & Integer.MIN_VALUE) == 0) ? 0 : -1;
		int bytesWritten = 0;
		while (hasMore) {
			hasMore = (remaining != end)
					|| ((remaining & 1) != ((value >> 6) & 1));
			
			buf.writeByte((byte) ((value & SEGMENT_BITS) | (hasMore ? CONTINUE_BIT : 0)));
			++bytesWritten;
			value = remaining;
			remaining >>= 7;
		}
		
		return buf;
	}
	
	public static final int writePosVarIntCount(int i, ByteBuf buf) {
		int totalBytes = 0;
		while((i & ~SEGMENT_BITS) != 0) {
			buf.writeByte((i & SEGMENT_BITS) | CONTINUE_BIT);
			++totalBytes;
			i >>>= 7;
		}
		buf.writeByte(i);
		return ++totalBytes;
	}
	
	public static final ByteBuf writePosVarInt(int i, ByteBuf buf) {
		while((i & ~SEGMENT_BITS) != 0) {
			buf.writeByte((i & SEGMENT_BITS) | CONTINUE_BIT);
			i >>>= 7;
		}
		return buf.writeByte(i);
	}
	
	public static final int readVarInt(ByteBuf buf) {
		return readSignedVarInt(buf);
	}
	
	public static final int readPosVarInt(ByteBuf buf) {
		return readPosVarIntUnwrapped(buf);
	}
	
	public static final int readPosVarIntLoop(ByteBuf buf) {
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
	
	public static final int readPosVarIntUnwrapped(ByteBuf buf) {
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
	
	public static int readSignedVarInt(ByteBuf buf) {
		int result = 0;
		int cur;
		int count = 0;
		int signBits = -1;
		
		do {
			cur = buf.readByte() & SEGMENT_BITS;
			result |= (cur & SEGMENT_BITS) << (count * 7);
			signBits <<= 7;
			++count;
		} while (((cur & CONTINUE_BIT) == CONTINUE_BIT) && count < 5);
		
		if ((cur & CONTINUE_BIT) == CONTINUE_BIT) {
			throw new RuntimeException("invalid LEB128 sequence");
		}
		
		// Sign extend if appropriate
		if (((signBits >> 1) & result) != 0) {
			result |= signBits;
		}
		
		return result;
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
	
	public static final long readVarLong(ByteBuf buf) {
		return readVarLongUnwrapped(buf);
	}
	
	public static final long readVarLongLoop(ByteBuf buf) {
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
	
	public static final long readVarLongUnwrapped(ByteBuf buf) {
		
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
		public final boolean process(byte currentByte) throws Exception {
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
	
	public static final long readVarLongProcess(ByteBuf buf) {
		VarLongProcessor p = vlp.get().reset();
		buf.readerIndex(buf.forEachByte(p) + 1);
		return p.getValue();
	}
	
	
	public static final ByteBuf writeUByte(int value, ByteBuf buf) {
		return buf.writeByte(value);
	}
	
	public static final int readUByte(ByteBuf buf) {
		return buf.readByte() & 0xFF;
	}
	
	public static final ByteBuf writeVarArray(byte[] arr, ByteBuf buf) {
		return writePosVarInt(arr.length, buf)
				.writeBytes(arr);
	}
	
	public static final byte[] readVarArray(ByteBuf buf) {
		byte[] arr = new byte[readPosVarInt(buf)];
		buf.readBytes(arr);
		return arr;
	}
	
	public static final ByteBuf writeLongArray(long[] arr, ByteBuf buf) {
		writePosVarInt(arr.length, buf);
		for(int i=0; i<arr.length; ++i) {
			buf.writeLong(arr[i]);
		}
		return buf;
	}
	
	public static final long[] readLongArray(ByteBuf buf) {
		int length = readPosVarInt(buf);
		long[] arr = new long[length];
		for(int i=0; i<length; ++i) {
			arr[i] = buf.readLong();
		}
		return arr;
	}
	
	public static ByteBuf decryptSameFast(ByteBuf buf, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException  {
		int initialRead = buf.readerIndex();
		int enclength = buf.readableBytes();
		byte[] arr = tempArray(enclength);
		buf.readBytes(arr, 0, enclength);
		int plainLength = cipher.doFinal(arr, 0, enclength, arr);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		return buf.writeBytes(arr, 0, plainLength);
	}
	
	public static ByteBuf decryptSameFast(ByteBuf buf, HasCryptographicCredentials c) throws IllegalBlockSizeException, BadPaddingException, ShortBufferException  {
		return decryptSameFast(buf, decryptCipher(c));
	}
	
	public static ByteBuf decryptSame(ByteBuf buf, HasCryptographicCredentials c) throws IllegalBlockSizeException, BadPaddingException  {
		int initialRead = buf.readerIndex();
		byte[] enc = new byte[buf.readableBytes()];
		buf.readBytes(enc);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		byte[] plain = decryptCipher(c).doFinal(enc);
		return buf.writeBytes(plain);
	}
	
	public static ByteBuf decryptSame(ByteBuf buf, Cipher cipher) throws IllegalBlockSizeException, BadPaddingException  {
		return decryptSame(buf, buf.readableBytes(), cipher);
	}
	
	public static ByteBuf decryptSame(ByteBuf buf, int length, Cipher decCipher) throws IllegalBlockSizeException, BadPaddingException  {
		int initialRead = buf.readerIndex();
		byte[] encBytes = new byte[length];
		buf.readBytes(encBytes);
		byte[] plain = decCipher.doFinal(encBytes);
		buf.readerIndex(initialRead);
		buf.writerIndex(initialRead);
		return buf.writeBytes(plain);
	}
	
	public static ByteBuf decryptOther(ByteBuf plain, ByteBuf enc, Cipher decCipher) throws IllegalBlockSizeException, BadPaddingException {
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
	
	public static String readIntString(ByteBuf buf, Charset charset) {
		int length = buf.readInt();
		int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		String text = buf.toString(readIndex, length, charset);
		return text;
	}
	
	public static String readIntString(ByteBuf buf) {
		return readIntString(buf, DEFAULT_CHARSET);
	}
	
	public static String readByteString(ByteBuf buf, Charset charset) {
		int length = readUByte(buf);
		int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		String text = buf.toString(readIndex, length, charset);
		return text;
	}
	
	public static String readByteString(ByteBuf buf) {
		return readByteString(buf, DEFAULT_CHARSET);
	}
	
	public static String readVarIntString(ByteBuf buf, Charset charset) {
		int length = readPosVarInt(buf);
		int readIndex = buf.readerIndex();
		buf.skipBytes(length);
		return buf.toString(readIndex, length, charset);
	}
	
	public static String readVarString(ByteBuf buf) {
		return readVarIntString(buf, DEFAULT_CHARSET);
	}
	
	public static String readString(ByteBuf buf) {
		return readVarIntString(buf, DEFAULT_CHARSET);
	}
	
	public static final ByteBuf writeString(String s, ByteBuf buf) {
		return writeVarString(s, buf);
	}
	
	public static final ByteBuf writeVarString(String s, ByteBuf buf) {
		return writeVarIntString(s, DEFAULT_CHARSET, buf);
	}
	
	public static final ByteBuf writeVarIntString(String s, Charset charset, ByteBuf buf) {
		byte[] arr = s.getBytes(charset);
		return writeVarArray(arr, buf);
	}
	
	public static final ByteBuf writeByteString(String s, Charset charset, ByteBuf buf) {
		byte[] arr = s.getBytes(charset);
		writeUByte(arr.length, buf);
		return buf.writeBytes(arr);
	}
	
	public static final ByteBuf writeByteString(String s, ByteBuf buf) {
		return writeByteString(s, DEFAULT_CHARSET, buf);
	}
	
	public static final void writeShortString(String s, ByteBuf buf) {
		byte[] arr = s.getBytes();
		buf.writeShort(arr.length);
		buf.writeBytes(arr);
	}
	
	public static final void writeIntString(String s, ByteBuf buf) {
		byte[] arr = s.getBytes();
		buf.writeInt(arr.length);
		buf.writeBytes(arr);
	}
	
	public static final void encode(PublicKey key, ByteBuf buf) {
		buf.writeBytes(key.getEncoded());
	}
	
	public static final PublicKey decodeRSAPublicKey(ByteBuf buf) throws InvalidKeySpecException, NoSuchAlgorithmException {
		byte[] arr = new byte[buf.readableBytes()];
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
	
	public static final byte[] compress(byte[] data) {
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
	
	public static final ByteBuf compressSameAsArray(ByteBuf buf) {
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
	
	public static final byte[] decompress(byte[] compressedBytes) {
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
	
	public static final ByteBuf decompressSame(ByteBuf buf) {
		return EncodingUtils.encodeSame(EncodingUtils::decompress, buf);
	}
	
	public static final ByteBuf decompressNew(ByteBuf in, ByteBuf out) {
		return EncodingUtils.encodeTo(EncodingUtils::decompress, in, out);
	}
	
	public static final ByteBuf decompressNew(Channel channel, ByteBuf in) {
		return EncodingUtils.encodeTo(EncodingUtils::decompress, in, channel.alloc().buffer(in.readableBytes()));
	}
	
	public static String toBinaryString(byte b) {
		return String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
	}
	
	public static String toBinaryString(int i) {
		return String.format("%8s", Integer.toBinaryString(i & 0xFF)).replace(' ', '0');
	}
	
	
	
	private static String getSingleHexChar(int i) {
		return hexChars[i];
	}
	
	public static void newPretty(StringBuilder builder, byte b) {
		for(int i=7; i>=0; --i) {
			int bit = (b >> i) & 1;
			builder.append(bit);
		}
	}
	
	public static void newPretty(StringBuilder b, byte[] bytes) {
		b.append("[");
		if(bytes.length > 0)
			b.append("\n");
		for(int i=0; i<bytes.length; ++i)
			newPretty(b, bytes[i]);
		b.append("\n]");
	}
	
	public static void pretty(StringBuilder b, byte[] bytes) {
		b.append("[");
		if(bytes.length > 0)
			b.append("\n");
		for(int i=0; i<bytes.length; ++i)
			b.append(toBinaryString(bytes[i])).append("\n");
		b.append("]");
	}
	
	public static String pretty(byte[] bytes) {
		StringBuilder builder = new StringBuilder();
		pretty(builder, bytes);
		return builder.toString();
	}
	
	public static String getPrettyHexTableIndex(int index) {
		int row = index / 16;
		int column = index % 16;
		return toBinaryString(row) + ":" + getSingleHexChar(column);
	}
	
}
