package com.aimir.fep.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class AESUtil {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
        try { 
        	String originalText = "0000000000000001"+"0000000000000002"+"0000000000000003"+"0000000000000004"+"0000000000000005";
        	String key = "000B120000000999";	
        	String en = encrypt( originalText, key);
        	String de = decrypt( en, key);
        	
        	System.out.println( "Original Text is " + originalText);
        	System.out.println( "Encrypted Text is " + en );
        	System.out.println( "Decrypted Text is " + de );
        	
        	originalText = getHashcode(Hex.encode(key));
        	en = encrypt( originalText, key);
        	de = decrypt( en, key);
        	
        	System.out.println( "Original Text is " + originalText);
        	System.out.println( "Encrypted Text is " + en );
        	System.out.println( "Decrypted Text is " + de );

        } catch (Exception ex) {
        	ex.printStackTrace();
        }
	}
	
    public static String getHashcode(byte[] messageKey) throws NoSuchAlgorithmException {

        //MessageDigest md = MessageDigest.getInstance("SHA-256");
    	MessageDigest md = MessageDigest.getInstance("SHA-512");
        // 해싱할 byte배열을 넘겨준다.
        // SHA-256의 경우 메시지로 전달할 수 있는 최대 bit 수는 2^64-1개 이다.
        md.update(messageKey);
 
        // 해싱된 byte 배열을 digest메서드의 반환값을 통해 얻는다.
        byte[] hashbytes = md.digest();
 
        // 보기 좋게 16진수로 만드는 작업
        StringBuilder sbuilder = new StringBuilder();
        //for (int i = 0; i < hashbytes.length; i++) {
        for (int i = 0; i < 40; i++) {
            // %02x 부분은 0 ~ f 값 까지는 한자리 수이므로 두자리 수로 보정하는 역할을 한다.
            sbuilder.append(String.format("%02x", hashbytes[i] & 0xff));
        }
 
        return sbuilder.toString().toUpperCase();
    }
    
    public static String decrypt(String text, String key) throws Exception

    {
    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	byte[] keyBytes= new byte[16];
    	byte[] b= key.getBytes("UTF-8");
    	int len= b.length;
    	if (len > keyBytes.length) len = keyBytes.length;
    	System.arraycopy(b, 0, keyBytes, 0, len);
    	SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    	IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
    	cipher.init(Cipher.DECRYPT_MODE,keySpec,ivSpec);


    	BASE64Decoder decoder = new BASE64Decoder();
    	byte [] results = cipher.doFinal(decoder.decodeBuffer(text));
    	return new String(results,"UTF-8");
    }

    public static String encrypt(String text, String key) throws Exception

    {
    	Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    	byte[] keyBytes= new byte[16];
    	byte[] b= key.getBytes("UTF-8");
    	int len= b.length;
    	if (len > keyBytes.length) len = keyBytes.length;
    	System.arraycopy(b, 0, keyBytes, 0, len);
    	SecretKeySpec keySpec = new SecretKeySpec(keyBytes, "AES");
    	IvParameterSpec ivSpec = new IvParameterSpec(keyBytes);
    	cipher.init(Cipher.ENCRYPT_MODE,keySpec,ivSpec);
    	 

    	byte[] results = cipher.doFinal(text.getBytes("UTF-8"));
    	BASE64Encoder encoder = new BASE64Encoder();
    	return encoder.encode(results);
    }
}
