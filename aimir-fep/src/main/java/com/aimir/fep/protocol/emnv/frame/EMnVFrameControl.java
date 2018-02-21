/**
 * (@)# EMnVFrameControl.java
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

import java.math.BigInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlAck;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlAddr;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlSecurity;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class EMnVFrameControl {
	private static Logger log = LoggerFactory.getLogger(EMnVFrameControl.class);

	private FrameControlAddr DST_ADDR_TYPE;
	private FrameControlAddr SRC_ADDR_TYPE;
	//private int RESERVED1;
	//private int RESERVED2;
	private FrameControlSecurity SECURITY_ENABLE;
	private FrameControlAck ACK_REQ_ENABLE;
	
	public EMnVFrameControl() {
		
	}

	public EMnVFrameControl(byte[] frameControl) {
		decode(frameControl);
	}

	public FrameControlAddr getDST_ADDR_TYPE() {
		return DST_ADDR_TYPE;
	}

	public void setDST_ADDR_TYPE(FrameControlAddr dST_ADDR_TYPE) {
		DST_ADDR_TYPE = dST_ADDR_TYPE;
	}

	public FrameControlAddr getSRC_ADDR_TYPE() {
		return SRC_ADDR_TYPE;
	}

	public void setSRC_ADDR_TYPE(FrameControlAddr sRC_ADDR_TYPE) {
		SRC_ADDR_TYPE = sRC_ADDR_TYPE;
	}

	public FrameControlSecurity getSECURITY_ENABLE() {
		return SECURITY_ENABLE;
	}

	public void setSECURITY_ENABLE(FrameControlSecurity sECURITY_ENABLE) {
		SECURITY_ENABLE = sECURITY_ENABLE;
	}

	public FrameControlAck getACK_REQ_ENABLE() {
		return ACK_REQ_ENABLE;
	}

	public void setACK_REQ_ENABLE(FrameControlAck aCK_REQ_ENABLE) {
		ACK_REQ_ENABLE = aCK_REQ_ENABLE;
	}

	
	public void decode(byte[] framControl) {
		DST_ADDR_TYPE = FrameControlAddr.getItem(framControl[0] >>> 4);
		SRC_ADDR_TYPE = FrameControlAddr.getItem(framControl[0] & 0x0f);
		//RESERVED1 = (framControl[1] >>> 4);
		//RESERVED2 = ((framControl[1] & 0x0f) >>> 3);
		SECURITY_ENABLE = FrameControlSecurity.getItem((framControl[1] & 0x07) >>> 2);
		ACK_REQ_ENABLE = FrameControlAck.getItem((framControl[1] & 0x03) >>> 1);
		//RESERVED3 = (framControl[1] & 0x01);
	}
	
	public byte[] encode(){
		byte[] result = new byte[2];
		
		String byteString = "";
		byteString += String.format("%04d", new BigInteger(Integer.toBinaryString(DST_ADDR_TYPE.getValue())));   // Destination address type
		byteString += String.format("%04d", new BigInteger(Integer.toBinaryString(SRC_ADDR_TYPE.getValue())));   // Source      address type
		result[0] = (byte)Integer.parseInt(byteString, 2);
		
		byteString = "0000";       // Reserved
		byteString += "0";          // Reserved
		byteString += String.valueOf(SECURITY_ENABLE.getValue());  // Security enabled
		byteString += String.valueOf(ACK_REQ_ENABLE.getValue());  // Ack request
		byteString += "0";          // Reserved
		result[1] = (byte)Integer.parseInt(byteString, 2);
		
		log.info("[PROTOCOL][GENERAL_FRAME_FORMAT] FRAME_CONTROL(2)][ENCODE]: DST_ADDR_TYPE={}, SRC_ADDR_TYPE={}"
				+ ", SECURITY_ENABLE={}, ACK_REQ_ENABLE={} | ByteString={}, Hex={}"
				, new Object[]{DST_ADDR_TYPE.name(), SRC_ADDR_TYPE.name()
						, SECURITY_ENABLE.name(), ACK_REQ_ENABLE.name(), byteString, Hex.getHexDump(result)});
		return result;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("EMnVFrameControl [DST_ADDR_TYPE=");
		builder.append(DST_ADDR_TYPE);
		builder.append(", SRC_ADDR_TYPE=");
		builder.append(SRC_ADDR_TYPE);
		builder.append(", SECURITY_ENABLE=");
		builder.append(SECURITY_ENABLE);
		builder.append(", ACK_REQ_ENABLE=");
		builder.append(ACK_REQ_ENABLE);
		builder.append("]");
		return builder.toString();
	}


	
}
