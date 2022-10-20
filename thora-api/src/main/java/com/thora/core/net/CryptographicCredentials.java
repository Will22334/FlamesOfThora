package com.thora.core.net;

import java.util.Objects;

import javax.crypto.Cipher;

public abstract class CryptographicCredentials {
	
	public abstract AsymmetricKeyCipher getAssymetric();
	public abstract SymmetricKeyCipher getSymmetric();
	
	public Cipher symmetricEnc() {
		return getSymmetric().getEncryptCipher();
	}
	
	public Cipher symmetricDec() {
		return getSymmetric().getDecryptCipher();
	}
	
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
	@Override
	public int hashCode() {
		return Objects.hash(getAssymetric(), getSymmetric());
	}
	@Override
	public boolean equals(Object o) {
		if(o instanceof CryptographicCredentials) {
			CryptographicCredentials c = (CryptographicCredentials) o;
			return Objects.equals(getAssymetric(), c.getAssymetric()) &&
					Objects.equals(getSymmetric(), c.getSymmetric());
		}
		return super.equals(o);
	}
	
}
