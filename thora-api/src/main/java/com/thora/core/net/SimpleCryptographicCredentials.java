package com.thora.core.net;

import java.security.KeyPair;

public class SimpleCryptographicCredentials extends CryptographicCredentials {
	
	public static SimpleCryptographicCredentials fromKeyPair(KeyPair pair) {
		
		return null;
	}
	
	public AsymmetricKeyCipher assymetricKey;
	public SymmetricKeyCipher symmetricKey;
	
	public SimpleCryptographicCredentials(AsymmetricKeyCipher assymetricKey, SymmetricKeyCipher symmetricKey) {
		super();
		this.assymetricKey = assymetricKey;
		this.symmetricKey = symmetricKey;
	}
	
	public SimpleCryptographicCredentials() {
		this(null, null);
	}

	@Override
	public AsymmetricKeyCipher getAssymetric() {
		return assymetricKey;
	}
	
	@Override
	public SymmetricKeyCipher getSymmetric() {
		return symmetricKey;
	}
	
}