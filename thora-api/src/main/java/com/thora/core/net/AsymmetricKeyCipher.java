package com.thora.core.net;

import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Objects;
import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

public class AsymmetricKeyCipher extends KeyCipher {
	
	public static AsymmetricKeyCipher createSafe(KeyPair pair) {
		try {
			return create(pair);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static AsymmetricKeyCipher create(KeyPair pair)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Objects.requireNonNull(pair, "Cannot create AsymmCipher with null KeyPair!");
		Cipher dc = generateCipher(pair.getPrivate(), Cipher.DECRYPT_MODE);
		Cipher ec = generateCipher(pair.getPublic(), Cipher.ENCRYPT_MODE);
		return new AsymmetricKeyCipher(pair.getPrivate(), dc, pair.getPublic(), ec);
	}
	
	public static AsymmetricKeyCipher createSafe(PublicKey key) {
		try {
			return create(key);
		} catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static AsymmetricKeyCipher create(PublicKey key)
			throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		Objects.requireNonNull(key, "Cannot create public AsymmCipher with null publicKey!");
		Cipher ec = generateCipher(key, Cipher.ENCRYPT_MODE);
		return new AsymmetricKeyCipher(key, ec);
	}
	
	private static Cipher generateCipher(Key key, int mode)
			throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException {
		Cipher c = Cipher.getInstance(key.getAlgorithm());
		c.init(mode, key);
		return c;
	}
	
	protected final PrivateKey privateKey;
	protected final PublicKey publickKey;
	
	protected Cipher encCipher, decCipher;
	
	public AsymmetricKeyCipher(PrivateKey privateKey, Cipher decCipher,
			PublicKey publicKey, Cipher encCipher) {
		this.privateKey = privateKey;
		this.decCipher = decCipher;
		this.publickKey = publicKey;
		this.encCipher = encCipher;
	}
	
	public AsymmetricKeyCipher(PrivateKey privateKey, Function<PrivateKey,Cipher> privCipherProtocol,
			PublicKey publicKey, Function<PublicKey,Cipher> pubCipherProtocol) {
		this(privateKey, privCipherProtocol.apply(privateKey),
				publicKey, pubCipherProtocol.apply(publicKey));
	}
	
	public AsymmetricKeyCipher(PublicKey publicKey, Function<PublicKey,Cipher> cipherProtocol) {
		this(null, null, publicKey, cipherProtocol.apply(publicKey));
	}
	
	public AsymmetricKeyCipher(PublicKey publicKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {
		this(null, null, publicKey, generateCipher(publicKey, Cipher.ENCRYPT_MODE));
	}
	
	public AsymmetricKeyCipher(PublicKey publicKey, Cipher encCipher) {
		this(null, null, publicKey, encCipher);
	}
	
	public final PrivateKey getPrivateKey() {
		return privateKey;
	}
	
	public final PublicKey getPublicKey() {
		return publickKey;
	}
	
	public final boolean isKeyOwner() {
		return getPrivateKey() != null;
	}
	
	@Override
	public final Kind getKind() {
		return Kind.ASYMMETRIC;
	}
	
	@Override
	public final Cipher getEncryptCipher() {
		return encCipher;
	}
	
	@Override
	public final Cipher getDecryptCipher() {
		return decCipher;
	}
	
	public String getAlgorithm() {
		return getEncryptCipher().getAlgorithm();
	}
	
}
