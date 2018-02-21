/**
 * (@)# RequestLink.java
 *
 * 2015. 6. 22.
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

import com.aimir.fep.protocol.emnv.frame.EMnVConstants.EMnVLinkSubFrameType;

/**
 * @author simhanger
 *
 */
public class RequestLink implements ILinkFrame {
	private static final long serialVersionUID = 1L;
	private static Logger log = LoggerFactory.getLogger(RequestLink.class);
	private EMnVLinkSubFrameType type = EMnVLinkSubFrameType.LINK_REQUEST;

	private int transactionId;      // 4byte
	private String date = null;     // 7byte, yyyyMMddhhmmss
	private String serverIp = null; // 4byte
	private int serverPort = 0;     // 2byte

	@Override
	public void decode(byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public byte[] encode() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isValidation(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

}
