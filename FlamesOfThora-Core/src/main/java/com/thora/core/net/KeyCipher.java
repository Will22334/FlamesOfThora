package com.thora.core.net;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public abstract class KeyCipher {
	
	public static enum Kind {
		SYMMETRIC(),
		ASYMMETRIC();
	}
	
	public abstract Kind getKind();
	public abstract Cipher getEncryptCipher();
	
	public byte[] encrypt(byte[] plain) throws IllegalBlockSizeException, BadPaddingException {
		return getEncryptCipher().doFinal(plain);
	}
	
	public final boolean canDecrypt() {
		return getDecryptCipher() != null;
	}
	
	public abstract Cipher getDecryptCipher();
	
	public byte[] decrypt(byte[] enc) throws IllegalBlockSizeException, BadPaddingException {
		return getDecryptCipher().doFinal(enc);
	}
	
}
