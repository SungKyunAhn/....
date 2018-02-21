package com.aimir.fep.util.sms.api.gabia;

import java.util.Base64;

public class Base64Utils {

	/**
	 * Base64Encoding 방식으로 바이트 배열을 아스키 문자열로 인코딩한다. In-Binany, Out-Ascii
	 * 
	 * @param encodeBytes
	 *            인코딩할 바이트 배열(byte[])
	 * @return 인코딩된 아스키 문자열(String)
	 */
	public static String encode(byte[] encodeBytes) {
		String strResult = null;

		try {
			strResult = Base64.getEncoder().encodeToString(encodeBytes);
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		return strResult;
	}

	/**
	 * Base64Decoding 방식으로 아스키 문자열을 바이트 배열로 디코딩한다. In-Ascii, Out-Binany
	 * 
	 * @param strDecode
	 *            디코딩할 아스키 문자열(String)
	 * @return 디코딩된 바이트 배열(byte[])
	 */
	public static byte[] decode(String strDecode) {
		byte[] buf = null;

		try {
			buf = Base64.getDecoder().decode(strDecode);
		} catch (Exception e) {
			System.out.println("Exception");
			e.printStackTrace();
		}
		return buf;
	}

	public static void main(String args[]) {
		String strOrgin = "Test String";
		String strDecode = null;
		byte[] bytOrgin = strOrgin.getBytes();

		System.out.println("OriginString=" + strOrgin);

		String strEncoded = Base64Utils.encode(bytOrgin);
		System.out.println("EncodedString=" + strEncoded);

		byte[] bytDecoded = Base64Utils.decode(strEncoded);
		strDecode = new String(bytDecoded);
		System.out.println("DecodedString=" + strDecode);
	}
}
