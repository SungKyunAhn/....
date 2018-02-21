/**
 * (@)# EMnVOTAFrame.java
 *
 * 2015. 4. 23.
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

import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVOTASubFrameType;
import com.aimir.fep.util.DataUtil;

/**
 * @author simhanger
 *
 */
//public class EMnVOTAFramePayLoad implements EMnVGeneralFramePayLoad {
public class EMnVOTAFramePayLoad extends EMnVGeneralFramePayLoad {
	private static final long serialVersionUID = 1L;
	private byte[] payload_data;
	private EMnVOTASubFrameType frameType;
	private int frameLength = 23;

	// Upgrade Start Request
	private Long imageLength; // 4byte, 송신 이미지의 전제 크기
	private byte[] imageCRC; // 2byte, 송신 이미지 전체의 CRC-16

	// Upgrade Start Response
	private String hwVersion; // 10byte, 모뎀의 H/W version(String) 
	private String fwVersion; // 10byte, 모뎀의 F/W version(String)
	private int availablePacketSize; // 4byte, 한번에 수신 가능한 image data의 크기

	// Upgrade Data
	private int imageAddress; // 4byte, 전송되는 이미지의 상대주소(전송 이미지의 시작 번지)
	private int imageSize; // 2byte, 현재 payload에 들어있는 image data의 크기(N)
	private byte[] imageData; // N, 실제 이미지 데이터

	// Upgrade End Response
	private int status = -1; // 1byte, 0:success, 1:error - imageCRC check fail

	public EMnVOTAFramePayLoad(byte[] payload_byte) {
		this.payload_data = payload_byte;
	}

	public EMnVOTAFramePayLoad(EMnVOTASubFrameType frameType) {
		this.frameType = frameType;
	}

	public byte[] getPayload_data() {
		return payload_data;
	}

	public void setPayload_data(byte[] payload_data) {
		this.payload_data = payload_data;
	}

	public Long getImageLength() {
		return imageLength;
	}

	public void setImageLength(Long imageLength) {
		this.imageLength = imageLength;
	}

	public byte[] getImageCRC() {
		return imageCRC;
	}

	public void setImageCRC(byte[] imageCRC) {
		this.imageCRC = imageCRC;
	}

	public String getHwVersion() {
		return hwVersion;
	}

	public void setHwVersion(String hwVersion) {
		this.hwVersion = hwVersion;
	}

	public String getFwVersion() {
		return fwVersion;
	}

	public void setFwVersion(String fwVersion) {
		this.fwVersion = fwVersion;
	}

	public int getAvailablePacketSize() {
		return availablePacketSize;
	}

	public void setAvailablePacketSize(int availablePacketSize) {
		this.availablePacketSize = availablePacketSize;
	}

	public int getImageAddress() {
		return imageAddress;
	}

	public void setImageAddress(int imageAddress) {
		this.imageAddress = imageAddress;
	}

	public int getImageSize() {
		return imageSize;
	}

	public void setImageSize(int imageSize) {
		this.imageSize = imageSize;
	}

	public byte[] getImageData() {
		return imageData;
	}

	public void setImageData(byte[] imageData) {
		this.imageData = imageData;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public EMnVOTASubFrameType getFrameType() {
		return frameType;
	}

	public void setFrameType(EMnVOTASubFrameType frameType) {
		this.frameType = frameType;
	}

	public int getFrameLength() {
		return frameLength;
	}

	@Override
	public boolean isValidation(Object obj) throws Exception {
		boolean result = true;

		switch (frameType) {
		case UPGRADE_START_RESPONSE:
			if (frameType == null || (hwVersion == null || hwVersion.equals("")) || (fwVersion == null || fwVersion.equals("")) || availablePacketSize == 0) {
				result = false;
			}			
			break;
		case UPGRADE_END_RESPONSE:
			if (frameType == null || status == -1) {
				result = false;
			}
			break;
		default:
			break;
		}

		return result;
	}

	@Override
	public void decode() throws Exception {
		frameType = EMnVOTASubFrameType.getItem(payload_data[0]);
		switch (frameType) {
		case UPGRADE_START_RESPONSE:
			byte[] hwVersionByte = new byte[10];
			System.arraycopy(payload_data, 1, hwVersionByte, 0, hwVersionByte.length);
			hwVersion = new String(hwVersionByte).trim();

			byte[] fwVersionByte = new byte[10];
			System.arraycopy(payload_data, 1 + hwVersionByte.length, fwVersionByte, 0, fwVersionByte.length);
			fwVersion = new String(fwVersionByte).trim();

			byte[] dataSizeByte = new byte[4];
			System.arraycopy(payload_data, 1 + hwVersionByte.length + fwVersionByte.length, dataSizeByte, 0, dataSizeByte.length);
			availablePacketSize = DataUtil.getIntTo4Byte(dataSizeByte);

			frameLength = hwVersionByte.length + fwVersionByte.length + dataSizeByte.length;
			break;
		case UPGRADE_END_RESPONSE:
			byte[] statusByte = new byte[1];
			System.arraycopy(payload_data, 1, statusByte, 0, statusByte.length);
			status = DataUtil.getIntToByte(statusByte[0]);
			frameLength = statusByte.length;
			break;
		default:
			break;
		}
	}

	@Override
	public byte[] encode() throws Exception {
		byte[] result = new byte[]{frameType.getData()};

		switch (frameType) {
		case UPGRADE_START_REQUEST:
			result = DataUtil.append(result, DataUtil.get4ByteToInt(imageLength));
			result = DataUtil.append(result, imageCRC);
			break;
		case UPGRADE_DATA:
            result = DataUtil.append(result, DataUtil.get4ByteToInt(imageAddress));
            result = DataUtil.append(result, DataUtil.get2ByteToInt(imageSize));
            result = DataUtil.append(result, imageData);
            
			break;
		case UPGRADE_END_REQUEST:
			// Frame Type만 보냄.
			break;
		default:
			break;
		}

		return result;
	}
}
