package com.thora.server;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

import com.thora.core.net.netty.EncodingUtils;

public class GenerateFreshKeyPair {
	
	public static final int RSA_KEY_LENGTH = 4096;
	
	public static void main(String[] args) throws Exception {
		
		System.out.println(Arrays.toString(Security.getProviders()));
		
		Path dir = Paths.get("./keys/");	
		dir = Files.createDirectories(dir);
		genRSAKeyPairAndSaveToFile(RSA_KEY_LENGTH, dir);
		
		System.out.println();
		
		PublicKey pub = readPublicKey(dir.resolve("publicKey"));
		PrivateKey priv = readPrivateKey(dir.resolve("privateKey"));
		
		KeyPair pair = EncodingUtils.readKeyPair(dir.resolve("publicKey"), dir.resolve("privateKey"));
		
	}
	
	public static void genRSAKeyPairAndSaveToFile(int keyLength, Path dir) {
		KeyPair keyPair = genRSAKeyPair(keyLength);
		
		PublicKey publicKey = keyPair.getPublic();
		PrivateKey privateKey = keyPair.getPrivate();
		
		DataOutputStream dos = null;
		try {
			String fullPath = dir.resolve("publicKey").toAbsolutePath().toString();
			File file = new File(fullPath);
			System.out.println("Writing " + publicKey.hashCode() + " to " + fullPath);
			file.createNewFile();
			dos = new DataOutputStream(new FileOutputStream(file));
			dos.write(publicKey.getEncoded());
			dos.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (dos != null) {
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
		}
		
		try {
			String fullPath = dir.resolve("privateKey").toAbsolutePath().toString();
			File file = new File(fullPath);
			System.out.println("Writing " + privateKey.hashCode() + " to " + fullPath);
			file.createNewFile();
			dos = new DataOutputStream(new FileOutputStream(file));
			dos.write(privateKey.getEncoded());
			dos.flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (dos != null)
				try {
					dos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}
	
	public static KeyPair genRSAKeyPair(int keyLength) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator
					.getInstance("RSA");
			keyPairGenerator.initialize(keyLength);
			return keyPairGenerator.generateKeyPair();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final PrivateKey readPrivateKey(Path path) throws Exception {
		
		byte[] keyBytes = Files.readAllBytes(path);
		
		PKCS8EncodedKeySpec spec =
				new PKCS8EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PrivateKey key = kf.generatePrivate(spec);
		System.out.println("Read " + key.hashCode() + " from " + path);
		return key;
		
	}
	
	public static PublicKey readPublicKey(Path path) throws Exception {
		
		byte[] keyBytes = Files.readAllBytes(path);
		
		X509EncodedKeySpec spec =
				new X509EncodedKeySpec(keyBytes);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey key = kf.generatePublic(spec);
		System.out.println("Read " + key.hashCode() + " from " + path);
		return key;
		
	}
	
}
