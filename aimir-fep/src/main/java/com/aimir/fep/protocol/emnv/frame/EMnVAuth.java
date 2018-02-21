/**
 * (@)# EMnVAuth.java
 *
 * 2015. 4. 22.
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
package com.aimir.fep.protocol.emnv.frame;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.dao.device.ModemDao;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;
import com.aimir.model.device.LTE;
import com.aimir.model.device.Modem;

/**
 * @author simhanger
 *
 */
public class EMnVAuth {
	private static Logger log = LoggerFactory.getLogger(EMnVAuth.class);

	private final byte[] IV = "HYBRID_KEPCO_SYS".getBytes();
	private byte[] auth_byte;
	private String imei;
	private byte[] authKey;

	private SecretKey sKey;
	private IvParameterSpec ivpSpec;
	private Cipher cipher;

//	@Autowired
//	ModemDao modemDao;

	public EMnVAuth(byte[] auth_byte) {
		this.auth_byte = auth_byte;
		imei = Hex.decode(auth_byte);
	}

	public String getModemImei() {
		return imei;
	}

	/**
	 * mobileNumbyte의 길이와 addImei 의 길이는 16byte여야함.
	 * 
	 * @param mobileNumByte
	 * @return
	 */
	public boolean authValidation(byte[] mobileNumByte) {
		boolean result = false;
		byte[] addImei = DataUtil.append(new byte[] { 0x00 }, auth_byte);

		if (mobileNumByte.length == 16 && addImei.length == 16) {
			byte[] temp = new byte[11];
			System.arraycopy(mobileNumByte, 5, temp, 0, temp.length);
			String deviceSerial = DataUtil.getString(temp).trim();

			ModemDao modemDao = DataUtil.getBean(ModemDao.class);
			Modem modem = modemDao.get(deviceSerial);
			if(modem == null){   // 최초 등록
				try {
					initCipher(addImei, mobileNumByte); // 초기화.
					result = true;
				} catch (Exception e) {
					log.warn("Auth Validation Error", e);
					result = false;
				}
			}else {
				LTE lteModem = (LTE) modem;
				String orgImei = lteModem.getImei();
				if (orgImei == null || orgImei.equals("") || orgImei.equals(imei)) {
					try {
						initCipher(addImei, mobileNumByte); // 초기화.
						result = true;
					} catch (Exception e) {
						log.warn("Auth Validation Error", e);
						result = false;
					}
				}				
			}
		} else {
			log.warn("Invalid Mobile nuumber length or Invalid IMEI length.");
		}

		return result;
	}

	/**
	 * Cipher 초기화
	 * 
	 * @param addImei
	 * @param mobileNumByte
	 * @throws Exception
	 */
	public void initCipher(byte[] addImei, byte[] mobileNumByte) throws Exception {
		if (cipher == null) {

			authKey = new byte[16];
			for (int i = 0; i < authKey.length; i++) {
				authKey[i] = (byte) (addImei[i] ^ mobileNumByte[i]);
			}

			SecretKeySpec skSpec = new SecretKeySpec(authKey, "ARIA"); // ARIA-128은 authKey의 길이가 16byte이어야하고 ARIA-256은 32byte여야한다. 
			SecretKeyFactory skFactory = SecretKeyFactory.getInstance("ARIA", "MJC");
			sKey = skFactory.generateSecret(skSpec);
			ivpSpec = new IvParameterSpec(IV);

			cipher = Cipher.getInstance("ARIA/CBC/PKCS7Padding", "MJC");
		}
	}

	private Cipher getCipher(int mode) throws InvalidKeyException, InvalidAlgorithmParameterException {
		if (cipher != null) {
			cipher.init(mode, sKey, ivpSpec);
		}

		return cipher;
	}

	public byte[] doDecryption(byte[] payload_byte, byte[] mobileNumByte) throws Exception {
		byte[] result = null;

		if (payload_byte != null && authValidation(mobileNumByte)) {
			result = getCipher(Cipher.DECRYPT_MODE).doFinal(payload_byte);
			log.info("[DECRYPTION][GENERAL_FRAME_FORMAT] CYPER_PAYLOAD DATA(n):[{}] ==> HEX=[{}]", "", Hex.decode(payload_byte));
			log.info("[DECRYPTION][GENERAL_FRAME_FORMAT] PLAIN_PAYLOAD DATA(n):[{}] ==> HEX=[{}]", "", Hex.decode(result));
		} else {
			byte[] temp = new byte[11];
			System.arraycopy(mobileNumByte, 5, temp, 0, temp.length);
			String deviceSerial = DataUtil.getString(temp).trim();
			
			log.info("#### AUTH 데이터 오류 - {} ###", deviceSerial);
			log.info("#### AUTH 데이터 오류 - {} ###", deviceSerial);
			log.info("#### AUTH 데이터 오류 - {} ###", deviceSerial);

			throw new EMnVSystemException(EMnVExceptionReason.INVALID_AUTH_PROTOCOL);
		}
		return result;
	}

	public byte[] doEncryption(byte[] payload_byte) throws Exception {
		byte[] result = null;

		if (payload_byte != null) {
			result = getCipher(Cipher.ENCRYPT_MODE).doFinal(payload_byte);
			log.info("[ENCRYPTION][GENERAL_FRAME_FORMAT] PLAIN_PAYLOAD DATA(n):[{}] ==> HEX=[{}]", "", Hex.decode(payload_byte));
			log.info("[ENCRYPTION][GENERAL_FRAME_FORMAT] CYPER_PAYLOAD DATA(n):[{}] ==> HEX=[{}]", "", Hex.decode(result));
		}

		return result;
	}

	public byte[] getAuth_byte() {
		log.debug("[ENCODE] AUTH_BYTE = {}", Hex.decode(auth_byte));
		return auth_byte;
	}
}
