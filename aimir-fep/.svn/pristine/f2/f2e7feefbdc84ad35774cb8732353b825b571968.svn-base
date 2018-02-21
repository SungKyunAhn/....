/**
 * (@)# SubgigaModemInfo.java
 *
 * 2015. 4. 29.
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
package com.aimir.fep.protocol.emnv.frame.payload;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.constants.CommonConstants.ModemType;
import com.aimir.fep.util.DataUtil;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class SubgigaModemInfo implements IModemInfo {
	private static final long serialVersionUID = 1L;

	private static Logger log = LoggerFactory.getLogger(SubgigaModemInfo.class);

	private ModemType modemType = ModemType.SubGiga;
	private final int totalLength = 88;
	private String deviceId;

	private byte[] fwVersion; // 10
	private byte[] hwVersion; // 10
	private byte[] euiId; // 8 , Modem EUI ID
	private byte[] prodCompany; // 20, 제작사     / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] prodDate; // 20, 제조년월일 / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)
	private byte[] prodNumber; // 20, 제조번호   / 문자열 – xxxxxxxxxxxxxxxx(20자리) / (상위부터 값을 채우고 하위 bytes가 없을시 나머지는 0을 채운다.)

	public SubgigaModemInfo(String sourceAddress) {
		fwVersion = new byte[10];
		hwVersion = new byte[10];
		euiId = new byte[8];
		prodCompany = new byte[20];
		prodDate = new byte[20];
		prodNumber = new byte[20];
	}

	public byte[] getFwVersion() {
		return fwVersion;
	}

	public byte[] getHwVersion() {
		return hwVersion;
	}

	public byte[] getEuiId() {
		return euiId;
	}

	public byte[] getProdCompany() {
		return prodCompany;
	}

	public byte[] getProdDate() {
		return prodDate;
	}

	public byte[] getProdNumber() {
		return prodNumber;
	}

	@Override
	public int getTotalLength() {
		return totalLength;
	}

	
	@Override
	public void decode(byte[] data) {
		try {
			log.debug("[PROTOCOL][MODEM_INFO][{}] SUB_GIGA_MODEM_INFO ({}):[{}] ==> {}", new Object[]{modemType.name(), data.length, "", Hex.decode(data)});
			
			System.arraycopy(data, 0, fwVersion, 0, 10);
			System.arraycopy(data, 10, hwVersion, 0, 10);
			System.arraycopy(data, 20, euiId, 0, 8);
			
			System.arraycopy(data, 28, prodCompany, 0, 20);
			prodCompany = DataUtil.trim0x00Byte(prodCompany);
			
			System.arraycopy(data, 48, prodDate, 0, 20);
			prodDate = DataUtil.trim0x00Byte(prodDate);
			
			System.arraycopy(data, 68, prodNumber, 0, 20);
			prodNumber = DataUtil.trim0x00Byte(prodNumber);
			
			log.debug("[PROTOCOL][MODEM_INFO][{}] SUB_GIGA_MODEM_INFO ({}):[{}] ==> {}", new Object[]{modemType.name(), data.length, "", toString()});
		} catch (Exception e) {
			log.debug("SubgigaModemInfo decode error - {}", e);
		}
	}

	@Override
	public byte[] encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ModemType getModemType() {
		return modemType;
	}

	@Override
	public String getDeviceId() {
		return deviceId;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		try {
			builder.append("SubgigaModemInfo [modemType=");
			builder.append(modemType);
			builder.append(", totalLength=");
			builder.append(totalLength);
			builder.append(", deviceId=");
			builder.append(deviceId);
			builder.append(", fwVersion=");
			builder.append(new String(fwVersion));
			builder.append(", hwVersion=");
			builder.append(new String(hwVersion));
			builder.append(", euiId=");
			builder.append(new String(euiId));
			builder.append(", prodCompany=");
			builder.append(new String(prodCompany));
			builder.append(", prodDate=");
			builder.append(new String(prodDate));
			builder.append(", prodNumber=");
			builder.append(new String(prodNumber));
			builder.append("]");
		} catch (Exception e) {
			log.debug("SubgigaModemInfo toString Error - {}", e);
		}

		return builder.toString();
	}


}
