package com.zerobase.domain.util;

import org.apache.tomcat.util.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class Aes256Util {
	public static String alg = "AES/CBC/PKCS5Padding";
	private static final String KEY = "ZEROBASEKEYISZEROBASEKEY";
	private static final String IV = KEY.substring(0, 16);

	public static String encrypt(String text) {
		try{
    		Cipher cipher = Cipher.getInstance(alg);
			SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);
			byte[] encrypted = cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));
			return Base64.encodeBase64String(encrypted); // encrypted 된 바이트를 base64 로 반환하여 더 암호화 시킴
  		} catch (Exception e) {
			return null;
		}
	}

	public static String decrypt(String cipherText) {
		try{
			Cipher cipher = Cipher.getInstance(alg);
			SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
			IvParameterSpec ivParameterSpec = new IvParameterSpec(IV.getBytes(StandardCharsets.UTF_8));
			cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

			byte[] decodeBytes = Base64.decodeBase64(cipherText);
			byte[] decrypted = cipher.doFinal(decodeBytes);
			return new String(decrypted, StandardCharsets.UTF_8);
		  }catch (Exception e){
			return null;
		  }

	}

}
