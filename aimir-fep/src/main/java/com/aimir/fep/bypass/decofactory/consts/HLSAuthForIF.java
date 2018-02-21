/**
 * (@)# HLSAuthForIF.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.XDLMS_APDU;
import com.aimir.fep.bypass.decofactory.consts.DlmsConstantsForEVN.DlmsPiece;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class HLSAuthForIF {
	private static Logger logger = LoggerFactory.getLogger(HLSAuthForIF.class);

	private HLSSecurityControl SC;
	private final int tLen = 12 * Byte.SIZE; // 96 bit

	//private byte[] EK; // DEDICATED_KEY를 이용해야한다. global의 경우는 최초한번 만든걸로 고정해서 쓰지만 dedicate ciphering은 세션마다 랜덤하게 만들어야한다.  !!! 이렇게 했는데 안됨.ㅡ,.ㅡ
	private final String EK = "00000000000000000000000000000000";
	private final String AK = "00000000000000000000000000000000";
	
	public static enum HLSSecurityControl {
		NONE(0x00), COMPRESSION(0x80), AUTHENTICATION_ONLY(0x10), ENCRYPTION_ONLY(0x20), AUTHENTICATION_ENCRYPTION(0x30);

		private int value;

		private HLSSecurityControl(int value) {
			this.value = value;
		}

		public byte[] getValue() {
			return new byte[] { (byte) value };
		}

		public static HLSSecurityControl getItem(byte value) {
			for (HLSSecurityControl fc : HLSSecurityControl.values()) {
				if (fc.value == value) {
					return fc;
				}
			}
			return null;
		}

	}

	public HLSAuthForIF(HLSSecurityControl securityMode) throws Exception {
		this.SC = securityMode;
		logger.info("[HLS AUTH MODE] Security Control = [{}][{}]", SC.name(), Hex.decode(SC.getValue()));
	}

	private Cipher getCipher(int mode, byte[] iv, byte[] aad) throws Exception {
		SecretKey sKey = new SecretKeySpec(DataUtil.readByteString(EK), "AES");
		GCMParameterSpec params = new GCMParameterSpec(tLen, iv);

		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding", "SunJCE");		
		cipher.init(mode, sKey, params);
		
		if(aad != null)
		cipher.updateAAD(aad);
		

		return cipher;
	}

	public byte[] doEncryption(byte[] ic, byte[] apTitle, byte[] information) {
		byte[] result = null;

		try {
			if (ic != null && apTitle != null && information != null) {
				/*
				 * P(plainText) = information
				 * EK:16byte
				 * IV:12byte = Sys-T(apTitle:8byte) + IC:4byte
				 * AAD
				 *  - Authentication Only      : SC:1byte + AK:16byte + (C) Information
				 *  - Encryption Only          : null 
				 *  - Authenticated encryption : SC:1byte + AK:16byte
				 */
				byte[] IV = DataUtil.append(apTitle, ic);
				byte[] AAD = null;

				switch (SC) {
				case NONE:
					break;
				case COMPRESSION:
					break;
				case AUTHENTICATION_ONLY:
					AAD = DataUtil.append(DataUtil.append(SC.getValue(), DataUtil.readByteString(AK)), information);
					result = getCipher(Cipher.ENCRYPT_MODE, IV, AAD).doFinal(); // Authentication only 모드에서는 plainText를 쓰지 않는다
					break;
				case ENCRYPTION_ONLY:					
					result = getCipher(Cipher.ENCRYPT_MODE, IV, AAD).doFinal(information);
					break;
				case AUTHENTICATION_ENCRYPTION:
					AAD = DataUtil.append(SC.getValue(), DataUtil.readByteString(AK));
					result = getCipher(Cipher.ENCRYPT_MODE, IV, AAD).doFinal(information);
					break;
				default:
					break;
				}

				logger.info("[ENCRYPTION] IC    = [{}]", Hex.decode(ic));
				logger.info("[ENCRYPTION] Sys-T = [{}]", Hex.decode(apTitle));
				logger.info("[ENCRYPTION] IV    = [{}]", Hex.decode(IV));
				logger.info("[ENCRYPTION] AAD   = [{}]", Hex.decode(AAD));
				logger.info("[ENCRYPTION] Plain Text = [{}]", Hex.decode(information));
				logger.info("[ENCRYPTION] Cyper Text = [{}]", Hex.decode(result));
			}
		} catch (Exception e) {
			logger.error("HLSAuth doEncryption Error - {}", e);
			result = null;
		}

		return result;
	}

	public byte[] doDecryption(byte[] ic, byte[] apTitle, byte[] cipherText) {
		byte[] result = null;

		try {
			if (ic != null && apTitle != null && cipherText != null) {
				/*
				 * P(plainText) = information
				 * EK:16byte
				 * IV:12byte = Sys-T(apTitle:8byte) + IC:4byte
				 * AAD
				 *  - Authentication Only      : SC:1byte + AK:16byte + (C) Information
				 *  - Encryption Only          : null 
				 *  - Authenticated encryption : SC:1byte + AK:16byte
				 */
				byte[] IV = DataUtil.append(apTitle, ic);
				byte[] AAD = null;

				switch (SC) {
				case NONE:
					break;
				case COMPRESSION:
					break;
				case AUTHENTICATION_ONLY:
					AAD = DataUtil.append(DataUtil.append(SC.getValue(), DataUtil.readByteString(AK)), cipherText);
					result = getCipher(Cipher.DECRYPT_MODE, IV, AAD).doFinal(); // Authentication only 모드에서는 plainText를 쓰지 않는다
					break;
				case ENCRYPTION_ONLY:					
					result = getCipher(Cipher.DECRYPT_MODE, IV, AAD).doFinal(cipherText);
					break;
				case AUTHENTICATION_ENCRYPTION:
					AAD = DataUtil.append(SC.getValue(), DataUtil.readByteString(AK));
					result = getCipher(Cipher.DECRYPT_MODE, IV, AAD).doFinal(cipherText);
					break;
				default:
					break;
				}

				logger.info("[DECRYPTION] IC    = [{}]", Hex.decode(ic));
				logger.info("[DECRYPTION] Sys-T = [{}]", Hex.decode(apTitle));
				logger.info("[DECRYPTION] IV    = [{}]", Hex.decode(IV));
				logger.info("[DECRYPTION] AAD   = [{}]", Hex.decode(AAD));
				logger.info("[DECRYPTION] Cyper Text = [{}]", Hex.decode(cipherText));
				logger.info("[DECRYPTION] Plain Text = [{}]", Hex.decode(result));
			}
		} catch (Exception e) {
			logger.error("HLSAuth doDecryption Error - {}", e);
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

	public byte[] getReqEncryptionDedicateCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				XDLMS_APDU cyperType = XDLMS_APDU.DED_GET_REQUEST;
				reqValue = DataUtil.append(reqValue, cyperType.getValue()); // ded-get-request
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[GET-REQ:DEDICATED_CIPHERING] XDLMS-APDU Type = {} [{}]", cyperType.name(), Hex.decode(cyperType.getValue()));
				logger.info("[GET-REQ:DEDICATED_CIPHERING] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth getReqEncryptionDedicateCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}
	
	
	public byte[] setReqEncryptionDedicateCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				XDLMS_APDU cyperType = XDLMS_APDU.DED_SET_REQUEST;
				reqValue = DataUtil.append(reqValue, cyperType.getValue()); // ded-get-request
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[SET-REQ:DEDICATED_CIPHERING] XDLMS-APDU Type = {} [{}]", cyperType.name(), Hex.decode(cyperType.getValue()));
				logger.info("[SET-REQ:DEDICATED_CIPHERING] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth setReqEncryptionDedicateCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}

	public byte[] actionReqEncryptionDedicateCiphering(byte[] ic, byte[] information) {
		byte[] reqValue = new byte[] {};

		try {
			byte[] cipherText = new byte[] {};

			if (ic != null && information != null) {
				cipherText = doEncryption(ic, DlmsPiece.CLIENT_SYSTEM_TITLE.getBytes(), information);

				XDLMS_APDU cyperType = XDLMS_APDU.DED_ACTIONREQUEST;

				reqValue = DataUtil.append(reqValue, cyperType.getValue()); // ded-actionRequest
				reqValue = DataUtil.append(reqValue, new byte[1]); // length
				reqValue = DataUtil.append(reqValue, SC.getValue()); // SC
				reqValue = DataUtil.append(reqValue, ic); // IC
				reqValue = DataUtil.append(reqValue, cipherText); // Cipher Text
				reqValue[1] = DataUtil.getByteToInt(reqValue.length - 2); // request & request length 2바이트제외

				logger.info("[ACTION-REQ:DEDICATED_CIPHERING] XDLMS-APDU Type = {} [{}]", cyperType.name(), Hex.decode(cyperType.getValue()));
				logger.info("[ACTION-REQ:DEDICATED_CIPHERING] REQ_VALUE[APDU + LENGTH + SC + IC + CIPHER_TEXT(+TAG)] = [{}]", Hex.decode(reqValue));
			}
		} catch (Exception e) {
			logger.error("HLSAuth actionReqEncryptionDedicateCiphering Error - {}", e);
			reqValue = null;
		}

		return reqValue;
	}
}
