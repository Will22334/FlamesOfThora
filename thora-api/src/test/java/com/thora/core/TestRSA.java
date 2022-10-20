package com.thora.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.thora.core.net.AsymmetricKeyCipher;
import com.thora.core.net.netty.EncodingUtils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

public class TestRSA {
	
	@BeforeAll
	public static void init() {
		
	}
	
	@Test
	public void test() throws Exception {
		String algorithm = "RSA";
		int keyLength = 4096;
		KeyPairGenerator pairGen = KeyPairGenerator.getInstance(algorithm);
		pairGen.initialize(keyLength);
		
		KeyPair pair = pairGen.generateKeyPair();
		
		AsymmetricKeyCipher c = AsymmetricKeyCipher.create(pair);
		
		Assertions.assertNotNull(c, "AsymmCipher wrapper class generated is null!");
		Cipher dc = c.getDecryptCipher();
		Assertions.assertNotNull(dc, "DecryptCipher is null!");
		Cipher ec = c.getEncryptCipher();
		Assertions.assertNotNull(dc, "EncryptCipher is null!");
		
		ByteBuf buf = Unpooled.buffer();
		String sent = "ABCDEFGH";
		
		EncodingUtils.writeVarString(sent, buf);
		System.out.println("plain[" + buf.readableBytes() + "] = " + sent);
		
		EncodingUtils.encryptSame(buf, ec);
		int encLength = buf.readableBytes();
		System.out.println("enc[" + encLength + "]");
		
		
		EncodingUtils.decryptSame(buf, dc);
		int gotPlainLength = buf.readableBytes();
		String got = EncodingUtils.readVarString(buf);
		System.out.println("plain[" + gotPlainLength + "] = " + got);
	}
	
}
