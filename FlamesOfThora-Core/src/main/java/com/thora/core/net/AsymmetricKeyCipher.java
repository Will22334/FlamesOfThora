package com.thora.core.net;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.function.Function;

import javax.crypto.Cipher;

public class AsymmetricKeyCipher extends KeyCipher {
	
	protected final PrivateKey privateKey;
	protected final PublicKey publickKey;
	
	protected Cipher encCipher, decCipher;
	
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
	
	public AsymmetricKeyCipher(PrivateKey privateKey, Function<PrivateKey,Cipher> privCipherProtocol,
			PublicKey publicKey, Function<PublicKey,Cipher> pubCipherProtocol) {
		this.privateKey = privateKey;
		this.decCipher = privCipherProtocol.apply(privateKey);
		this.publickKey = publicKey;
		this.encCipher = pubCipherProtocol.apply(publicKey);
	}
	
	public AsymmetricKeyCipher(PublicKey publicKey, Function<PublicKey,Cipher> cipherProtocol) {
		this(publicKey, cipherProtocol.apply(publicKey));
	}
	
	public AsymmetricKeyCipher(PublicKey publicKey, Cipher encCipher) {
		this.privateKey = null;
		this.publickKey = publicKey;
		this.encCipher = encCipher;
	}
	
}
