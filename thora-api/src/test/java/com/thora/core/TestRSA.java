package com.thora.core;

import java.security.KeyPair;
import java.security.KeyPairGenerator;

import javax.crypto.Cipher;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.thora.core.net.AsymmetricKeyCipher;
import com.thora.core.net.netty.EncodingUtils;

import io.netty.buffer.ByteBuf;

public class TestRSA extends TestNetty {
	
	@Test
	public void test() throws Exception {
		final String algorithm = "RSA";
		final int keyLength = 4096;
		final KeyPairGenerator pairGen = KeyPairGenerator.getInstance(algorithm);
		pairGen.initialize(keyLength);
		
		final KeyPair pair = pairGen.generateKeyPair();
		
		final AsymmetricKeyCipher c = AsymmetricKeyCipher.create(pair);
		Assertions.assertNotNull(c, "AsymmCipher wrapper class generated is null!");
		
		final Cipher dc = c.getDecryptCipher();
		Assertions.assertNotNull(dc, "DecryptCipher is null!");
		
		final Cipher ec = c.getEncryptCipher();
		Assertions.assertNotNull(dc, "EncryptCipher is null!");
		
		
		final ByteBuf buf = TestNetty.getBuf();
		final String expectedMessage = "ABCDEFGH";
		
		EncodingUtils.writeIntString(expectedMessage, buf);
		//System.out.println("plain[" + buf.readableBytes() + "] = " + sent);
		
		EncodingUtils.encryptSame(buf, ec);
		int encLength = buf.readableBytes();
		//System.out.println("enc[" + encLength + "]");
		
		
		EncodingUtils.decryptSame(buf, dc);
		int gotPlainLength = buf.readableBytes();
		final String got = EncodingUtils.readIntString(buf);
		//System.out.println("plain[" + gotPlainLength + "] = " + got);
		
		Assertions.assertEquals(expectedMessage, got);
	}
	
}
