package com.thora.core.net;

import java.util.function.Function;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class SymmetricKeyCipher extends KeyCipher {
	
	protected SecretKey secretKey;
	protected Cipher encCipher, decCipher;
	
	@Override
	public final Kind getKind() {
		return Kind.SYMMETRIC;
	}
	
	@Override
	public final Cipher getEncryptCipher() {
		return encCipher;
	}
	
	@Override
	public final Cipher getDecryptCipher() {
		return decCipher;
	}
	
	public SymmetricKeyCipher(SecretKey secretKey, Function<SecretKey,Cipher> encGenerator
			, Function<SecretKey,Cipher> decGenerator) {
		this.secretKey = secretKey;
		this.encCipher = encGenerator.apply(secretKey);
		this.decCipher = decGenerator.apply(secretKey);
	}
	
}
