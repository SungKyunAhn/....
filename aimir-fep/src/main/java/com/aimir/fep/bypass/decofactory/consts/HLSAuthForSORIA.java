/**
 * (@)# HLSAuthForSORIA.java
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

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.security.HESPkiAPI;
import com.aimir.fep.protocol.security.OacServerApi;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class HLSAuthForSORIA {
	private static Logger logger = LoggerFactory.getLogger(HLSAuthForSORIA.class);

	private HLSSecurity securityMode;
	private final int tLen = 12 * Byte.SIZE; // 96 bit

	/*
	 * OAC에서 미터키를 받기위한 디바이스용 인증서를 받기위한 디바이스번호(고정값)
	 */
	private String HES_DEVICE_SERIAL;

	// Iraq MOE
	//	private final byte[] EK = { (byte) 0x00, (byte) 0x01, (byte) 0x02, (byte) 0x03, (byte) 0x04, (byte) 0x05, (byte) 0x06, (byte) 0x07, (byte) 0x08, (byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x0E, (byte) 0x0F };
	//	private final byte[] AK = { (byte) 0xD0, (byte) 0xD1, (byte) 0xD2, (byte) 0xD3, (byte) 0xD4, (byte) 0xD5, (byte) 0xD6, (byte) 0xD7, (byte) 0xD8, (byte) 0xD9, (byte) 0xDA, (byte) 0xDB, (byte) 0xDC, (byte) 0xDD, (byte) 0xDE, (byte) 0xDF };

	// SORIA 미터키 인증 적용하지 않을때 사용	
	//	private final byte[] EK = { (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0, (byte) 0xFE, (byte) 0xE0 };
	//	private final byte[] AK = { (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0, (byte) 0xFE, (byte) 0xA0 };

	// SORIA 미터키 인증 적용시 사용
	private byte[] EK;
	private byte[] AK;

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

	public HLSAuthForSORIA(HLSSecurity securityMode, String meterId) throws Exception {
		this.securityMode = securityMode;

		if (securityMode == null || meterId == null || meterId.equals("")) {
			throw new Exception("HLSAuth init error.");
		}

		logger.debug("HLS Security Mode={}, MeterId={}", securityMode.name(), meterId);

		Properties prop = new Properties();
		try {
			prop.load(getClass().getClassLoader().getResourceAsStream("config/fmp.properties"));
			if (prop.containsKey("protocol.security.hes.deviceSerial")) {
				this.HES_DEVICE_SERIAL = prop.getProperty("protocol.security.hes.deviceSerial");
			} else {
				this.HES_DEVICE_SERIAL = "000H000000000003";
			}
			logger.debug("### HES DEVICE SERIAL = {}", HES_DEVICE_SERIAL);
		} catch (IOException e) {
			logger.error("Properties loading error - {}", e.getMessage());
			throw new Exception("Properties loading error.");
		}

		switch (HLSSecurity.AUTHENTICATION) {
		case AUTHENTICATION:
			/*
			 * For Using MeterSharedKey
			 *
			logger.debug("### getMeterSharedKey ###");
			OacServerApi oacApi = new OacServerApi();
			HashMap<String, String> sharedKey = oacApi.getMeterSharedKey(HES_DEVICE_SERIAL, meterId);
			if (sharedKey != null) {
				String unicastKey = sharedKey.get("UnicastKey");
				String authKey = sharedKey.get("AuthenticationKey");
				String pinCode = sharedKey.get("pin_arg");

				logger.debug("##############  unicastKey={}", unicastKey);
				logger.debug("##############  authKey={}", authKey);
				logger.debug("##############  pinCode={}", pinCode);

				HESPkiAPI pkiApi = new HESPkiAPI();
				logger.debug("[UnicastKey Decrypting...][{}]", unicastKey);
				EK = pkiApi.doPkiDecrypt(DataUtil.readByteString(unicastKey), DataUtil.readByteString(pinCode));
				logger.debug("EK    = {}", Hex.decode(EK)); // encryption_unicast_key

				logger.debug("[AuthenticationKey Decrypting...][{}]", authKey);
				AK = pkiApi.doPkiDecrypt(DataUtil.readByteString(authKey), DataUtil.readByteString(pinCode));
				logger.debug("AK     = {}", Hex.decode(AK)); // authentication_key

			} else {
				throw new Exception("Can not find Shared Key.");
			}
			*/
			
			/*
			 * For Using Pana MeterSharedKey
			 */
			logger.debug("### getPanaMeterSharedKey ###");			
			OacServerApi oacApi = new OacServerApi();
			HashMap<String, String> sharedKey = oacApi.getPanaMeterSharedKey(HES_DEVICE_SERIAL, meterId);
			if (sharedKey != null) {
				String unicastKey = sharedKey.get("UnicastKey");
				String authKey = sharedKey.get("AuthenticationKey");
				String pinCode = sharedKey.get("pin_arg");

				logger.debug("##############  unicastKey={}", unicastKey);
				logger.debug("##############  authKey={}", authKey);
				logger.debug("##############  pinCode={}", pinCode);

				logger.debug("[UnicastKey Decrypting...][{}]", unicastKey);
				EK = DataUtil.readByteString(unicastKey);
				logger.debug("EK    = {}", Hex.decode(EK)); // encryption_unicast_key

				logger.debug("[AuthenticationKey Decrypting...][{}]", authKey);
				AK = DataUtil.readByteString(authKey);
				logger.debug("AK     = {}", Hex.decode(AK)); // authentication_key

			} else {
				throw new Exception("Can not find Shared Key.");
			}
			break;
		case AUTHENTICATION_ENCRYPTION:

			break;
		case ENCRYPTION:

			break;
		case NONE:

			break;
		default:
			break;
		}
	}

	/*
	 * RESTful Service로 Feph가 Fepa를 호출한다.
	 */
	public void getMeterKeyForHLS(String meterId) throws Exception {
		if (meterId == null || meterId.equals("")) {
			throw new Exception("HLSAuth init error - no meterId");
		}

	}

	private Cipher getCipher(int mode, byte[] iv, byte[] aad) throws Exception {
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
				logger.info("[ENCRYPTION] AAD_INFO   = [{}]", Hex.decode(aadInfor));
				logger.info("[ENCRYPTION] CYPER_TEXT = [{}]", Hex.decode(result));
			}
		} catch (Exception e) {
			logger.error("HLSAuth Encryption Error - {}", e);
			result = null;
		}

		return result;
	}

	public byte[] getTagValue(byte[] ic, byte[] apTitle, byte[] aadInfor) {
		byte[] tagValue = null;

		if (ic != null && apTitle != null && aadInfor != null) {
			byte[] cipherText = doEncryption(ic, apTitle, aadInfor);
			tagValue = Arrays.copyOfRange(cipherText, cipherText.length - (tLen / Byte.SIZE), cipherText.length);

			logger.info("[ENCRYPTION] TAG_VALUE = [{}]", Hex.decode(tagValue));
		}

		return tagValue;
	}

	public boolean doValidation(byte[] apTitle, byte[] ic, byte[] aadInfor, byte[] responseTagValue) {
		boolean result = false;

		if (ic != null && apTitle != null && aadInfor != null && responseTagValue != null) {
			byte[] myTagValue = getTagValue(ic, apTitle, aadInfor);
			result = Arrays.equals(responseTagValue, myTagValue);

			if (!result) {
				logger.debug("[Action Response Validation] Org TagValue = [{}], Response TagValue = [{}]", Hex.decode(myTagValue), Hex.decode(responseTagValue));
			}
		}

		return result;
	}

}
