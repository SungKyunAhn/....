/**
 * (@)# HLSAuthForMOE.java
 *
 * 2016. 4. 13.
 *
 * Copyright (c) 2013 NURITELECOM, Inc.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of 
 * NURITELECOM, Inc. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with NURITELECOM, Inc.
 *
 * For more information on this product, please see
 * http://www.nuritelecom.co.kr
 *
 */
package com.aimir.fep.bypass.decofactory.consts;

import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class HLSAuthForMOE {
	private static Log log = LogFactory.getLog(HLSAuthForMOE.class);

	private HLSSecurity securityMode;
	private final int tLen = 12 * Byte.SIZE;  // 96 bit
	
	
// Iraq MOE
//	private final byte[] EK = { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F };
//	private final byte[] AK = { (byte) 0xD0, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xDB, (byte) 0xDC, (byte) 0xDD, (byte) 0xDE, (byte) 0xDF };
                       
  	private final byte[] EK = { (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0 };
   	private final byte[] AK = { (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0 };

	public static enum HLSSecurity {
		NONE(0x00), AUTHENTICATION(0x10), ENCRYPTION(0x20), AUTHENTICATION_ENCRYPTION(0x30);

		private int value;

		private HLSSecurity(int value) {
			this.value = value;
		}

		public byte[] getValue() {
			return new byte[] { (byte) value };
		}
	}

	public HLSAuthForMOE(HLSSecurity securityMode) {
		this.securityMode = securityMode;
	}

	private Cipher getCipher(int mode, byte[] iv, byte[] aad) throws Exception{
		SecretKey sKey = new SecretKeySpec(EK, "AES");
		GCMParameterSpec params = new GCMParameterSpec(tLen, iv);
		
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");
		cipher.init(mode, sKey, params);
		cipher.updateAAD(aad);

		return cipher;
	}

	public byte[] doEncryption(byte[] ic, byte[] apTitle, byte[] aadInfor) {
		byte[] result = null;

		try {
			if (ic != null && apTitle != null && aadInfor != null) {
				/*
				 * P(plainText) : Authentication only 모드에서는 plainText를 쓰지 않는다.
				 * EK:16byte
				 * IV:12byte = Sys-T(apTitle:8byte) + IC:4byte
				 * AAD = SC:1byte + AK:16byte + P(aadInfor)
				 */
				byte[] IV = DataUtil.append(apTitle, ic);
				byte[] AAD = DataUtil.append(DataUtil.append(securityMode.getValue(), AK), aadInfor);

				result = getCipher(Cipher.ENCRYPT_MODE, IV, AAD).doFinal(); // Authentication only 모드에서는 plainText를 쓰지 않는다
				log.info("[ENCRYPTION] AAD_INFO   = [" + Hex.decode(aadInfor) + "]");
				log.info("[ENCRYPTION] CYPER_TEXT = [" + Hex.decode(result) + "]");
			}
		} catch (Exception e) {
			log.error("HLSAuth Encryption Error - " + e);
			result = null;
		}

		return result;
	}

	public byte[] getTagValue(byte[] ic, byte[] apTitle, byte[] aadInfor) {
		byte[] tagValue = null;

		if (ic != null && apTitle != null && aadInfor != null) {
			byte[] cipherText = doEncryption(ic, apTitle, aadInfor);
			tagValue = Arrays.copyOfRange(cipherText, cipherText.length - (tLen / Byte.SIZE), cipherText.length);
			
			log.info("[ENCRYPTION] TAG_VALUE = [" + Hex.decode(tagValue) + "]");
		}
		
		return tagValue;
	}
	
	public boolean doValidation(byte[] apTitle, byte[] ic,  byte[] aadInfor, byte[] responseTagValue) {
		boolean result = false;

		if (ic != null && apTitle != null && aadInfor != null && responseTagValue != null) {
			byte[] myTagValue = getTagValue(ic, apTitle, aadInfor);
			result = Arrays.equals(responseTagValue, myTagValue);
			
			if(!result){
				log.debug("[Action Response Validation] Org TagValue = [" + Hex.decode(myTagValue) + "] Response TagValue = [" + Hex.decode(responseTagValue) + "]");				
			}
		}

		return result;
	}

}
