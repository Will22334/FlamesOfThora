package com.thora.core.net;

import javax.crypto.Cipher;

public abstract class CryptographicCredentials {
	
	public abstract AsymmetricKeyCipher getAssymetric();
	public abstract SymmetricKeyCipher getSymmetric();
	
	public final Cipher encrypt() {
		SymmetricKeyCipher secret = getSymmetric();
		if(secret != null) {
			return secret.getEncryptCipher();
		} else {
			return getAssymetric().getEncryptCipher();
		}
	}
	
	public final Cipher decrypt() {
		SymmetricKeyCipher secret = getSymmetric();
		if(secret != null) {
			return secret.getDecryptCipher();
		} else {
			return getAssymetric().getDecryptCipher();
		}
	}
	
}
