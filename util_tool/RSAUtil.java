package com.secmask.util.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;

import javax.crypto.Cipher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RSAUtil {

	private static final Logger logger = LoggerFactory.getLogger(RSAUtil.class);
	
	public static String[] generateKeys() {
		String[] keys = new String[2];
		KeyPairGenerator keyPairGenerator = null;
		try {
			keyPairGenerator = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
		}
        SecureRandom secureRandom = new SecureRandom(new Date().toString().getBytes());
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        byte[] publicKeyBytes = keyPair.getPublic().getEncoded();
        byte[] privateKeyBytes = keyPair.getPrivate().getEncoded();
        keys[0] = BytesTranslate.bytesToHex(publicKeyBytes);
        keys[1] = BytesTranslate.bytesToHex(privateKeyBytes);
        return keys;
	}
	
	public static String saveKeys(String[] keys) {
		StringBuilder sb = new StringBuilder();
		sb.append("Public Key: " + keys[0]);
		sb.append(System.getProperty("line.separator"));
		sb.append("Private Key: " + keys[1]);
		
		FileOutputStream fos = null;
		String absolutePath = "";
		String filename = "keys_" + new Date().getTime() + ".rsa";
		File file = new File(filename);
		try {
			file.createNewFile();
			fos = new FileOutputStream(file);
			fos.write(sb.toString().getBytes("UTF-8"));
			absolutePath = file.getAbsolutePath();
		} catch (IOException e) {
			logger.error("保存RSA密钥对失败！", e);
		} finally {
			if(fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return absolutePath;
	}
	
	public static String decrypt(String keyHexStr, boolean isPublicKey, String msgHexStr) {
		return crypt(keyHexStr, isPublicKey, Cipher.DECRYPT_MODE, msgHexStr);
	}

	public static String encrypt(String keyHexStr, boolean isPublicKey, String msgHexStr) {
		return crypt(keyHexStr, isPublicKey, Cipher.ENCRYPT_MODE, msgHexStr);
	}

	//密钥、原始信息和结果信息都为16进制字符串
	private static String crypt(String keyHexStr, boolean isPublicKey, int opmode, String msgHexStr) {
		String result = "";
		try {
			byte[] keyBytes = BytesTranslate.hexToBytes(keyHexStr);
			KeyFactory keyFactory = KeyFactory.getInstance("RSA");
			Key key = null;
			if (isPublicKey) {
				key = keyFactory.generatePublic(new X509EncodedKeySpec(keyBytes));
			} else {
				key = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(keyBytes));
			}
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(opmode, key);
			byte[] cipherTextBytes = cipher.doFinal(BytesTranslate.hexToBytes(msgHexStr));
			result = BytesTranslate.bytesToHex(cipherTextBytes);
		} catch (Exception e) {
			logger.error("RSA加解密失败！", e);
		}
		return result;
	}

}
