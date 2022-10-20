package com.thora.core.net;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

public class AESKeyCipher extends SymmetricKeyCipher {
	
	private static Cipher genModed(SecretKey key, int mode) {
		try {
			
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(mode, key);
			return cipher;
			
		} catch(Exception e) {
			if(e instanceof RuntimeException) {
				throw (RuntimeException) e;
			} else {
				throw new RuntimeException(e);
			}
		}
	}
	
	private static Cipher genEnc(SecretKey key) {
		return genModed(key, Cipher.ENCRYPT_MODE);
	}
	
	private static Cipher genDec(SecretKey key) {
		return genModed(key, Cipher.DECRYPT_MODE);
	}
	
	public AESKeyCipher(SecretKey secretKey) {
		super(secretKey, AESKeyCipher::genEnc, AESKeyCipher::genDec);
	}
	
}
