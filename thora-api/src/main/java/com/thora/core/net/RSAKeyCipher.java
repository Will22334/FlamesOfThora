package com.thora.core.net;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class RSAKeyCipher extends AsymmetricKeyCipher {
	
	public static final RSAKeyCipher generateNew() {
		final KeyPair keyPair = keyPairGenerator.generateKeyPair();
		return new RSAKeyCipher(keyPair.getPrivate(), keyPair.getPublic());
	}
	
	private static KeyPairGenerator keyPairGenerator;
	
	static {
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(1024);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	private static Cipher generateDec(PrivateKey key) {
		Cipher c = null;
		try {
			c = Cipher.getInstance("RSA");
			c.init(Cipher.DECRYPT_MODE, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	
	private static Cipher generateEnc(PublicKey key) {
		Cipher c = null;
		try {
			c = Cipher.getInstance("RSA");
			c.init(Cipher.ENCRYPT_MODE, key);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return c;
	}
	
	public RSAKeyCipher(PrivateKey privateKey, PublicKey publicKey) {
		super(privateKey, RSAKeyCipher::generateDec, publicKey, RSAKeyCipher::generateEnc);
	}
	
	public RSAKeyCipher(KeyPair pair) {
		this(pair.getPrivate(), pair.getPublic());
	}
	
	public RSAKeyCipher(PublicKey publicKey) {
		super(publicKey, RSAKeyCipher.generateEnc(publicKey));
	}
	
}