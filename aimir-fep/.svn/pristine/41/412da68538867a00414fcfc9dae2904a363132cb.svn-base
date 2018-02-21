/**
 * (@)# EMnVLinkFramePayLoad.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.exception.EMnVSystemException;
import com.aimir.fep.protocol.emnv.exception.EMnVSystemException.EMnVExceptionReason;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVLinkSubFrameType;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
//public class EMnVLinkFramePayLoad implements EMnVGeneralFramePayLoad {
public class EMnVLinkFramePayLoad extends EMnVGeneralFramePayLoad {
	private static final long serialVersionUID = 1L;
	private static Logger logger = LoggerFactory.getLogger(EMnVLinkFramePayLoad.class);
	private byte[] payload_data;
	private EMnVLinkSubFrameType frameType; // 0:Link request, 1:Link response
	private ILinkFrame linkFrame;
	private final int frameLength = 23; 

	public EMnVLinkFramePayLoad(byte[] payload_byte) {
		payload_data = payload_byte;
	}

	public EMnVLinkSubFrameType getFrameType() {
		return frameType;
	}

	public ILinkFrame getLinkFrame() {
		return linkFrame;
	}

	public int getLinkFrameLength() {
		return frameLength;
	}
	
	@Override
	public boolean isValidation(Object obj) throws Exception {
		return linkFrame.isValidation(obj);
	}
	
	@Override
	public void decode() throws Exception {
		frameType = EMnVLinkSubFrameType.getItem(payload_data[0]);
		logger.info("[PROTOCOL][LINK_FRAME] FRAME_TYPE(1byte):[{}] ==> {}", frameType.name(), payload_data[0]);

		switch (frameType) {
		case LINK_REQUEST: // 추후 사용.
			//			linkFrame = new RequestLink();
			//			linkFrameLength = linkFrame.getTotalLength();
			break;
		case LINK_RESPONSE:
			linkFrame = new ResponseLink();
			break;
		default:
			throw new EMnVSystemException(EMnVExceptionReason.UNKNOWN, "Unknow LinkType");
		}

		byte[] info = new byte[frameLength];
		System.arraycopy(payload_data, frameType.getLength(), info, 0, frameLength);
		logger.info("[PROTOCOL][LINK_FRAME] FRAME_INFO({}):[{}] ==> {}", new Object[] { frameLength, "", Hex.decode(info) });
		linkFrame.decode(info);
	}

	@Override
	public byte[] encode() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}



}
