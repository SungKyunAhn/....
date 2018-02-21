/**
 * (@)# EMnVFrameUtil.java
 *
 * 2015. 6. 23.
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.emnv.actions.EMnVActions.EMnVActionType;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameControlAck;
import com.aimir.fep.protocol.emnv.frame.EMnVConstants.FrameType;

/**
 * @author simhanger
 *
 */
public class EMnVFrameUtil {
	private static Logger logger = LoggerFactory.getLogger(EMnVFrameUtil.class);

	public static EMnVGeneralDataFrame getGeneralDataFrame(FrameType type, EMnVGeneralDataFrame gDataFrame) {
		EMnVGeneralDataFrame gFrame = new EMnVGeneralDataFrame();
		gFrame.setSOF(EMnVConstants.General.SOF);
		//gFrame.setAuth(new EMnVAuth());
		gFrame.setAuth(gDataFrame.getAuth());
		gFrame.setFrameType(type);
		
		EMnVFrameControl vFrameControl = new EMnVFrameControl();
		vFrameControl.setDST_ADDR_TYPE(gDataFrame.getvFrameControl().getSRC_ADDR_TYPE());
		vFrameControl.setSRC_ADDR_TYPE(gDataFrame.getvFrameControl().getDST_ADDR_TYPE());
		vFrameControl.setSECURITY_ENABLE(gDataFrame.getvFrameControl().getSECURITY_ENABLE());
		vFrameControl.setACK_REQ_ENABLE(FrameControlAck.ACK_REQ_DISABLE);
		gFrame.setvFrameControl(vFrameControl);
		
		gFrame.setDestAddress(gDataFrame.getSourceAddress());
		gFrame.setSourceAddress(gDataFrame.getDestAddress());
		gFrame.setDest_addr_byte(gDataFrame.getSource_addr_byte());
		gFrame.setSource_addr_byte(gDataFrame.getDest_addr_byte());
		gFrame.setSequence(gDataFrame.getSequence() + 1);
		
		// Payload와 CRC는 만들지 않음.
		return gFrame;
	}
}
