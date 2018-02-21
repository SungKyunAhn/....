/**
 * (@)# SORIA_DLMSFrame.java
 *
 * 2016. 4. 15.
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
package com.aimir.fep.bypass.decofactory.decoframe;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcObjectType;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;
import com.aimir.fep.util.Hex;

/**
 * @author simhanger
 *
 */
public class SORIA_DLMSFrame implements INestedFrame {
	private static Logger logger = LoggerFactory.getLogger(SORIA_DLMSFrame.class);

	private byte[] gdDLMSFrame = null;
	private HdlcObjectType controlType;
	public Object resultData;
	private String meterId;
	private int[] meterRSCount = null;

	@Override
	public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
		logger.debug("## Excute SORIA_DLMSFrame Encoding [" + hdlcType.name() + "]");

		switch (hdlcType) {
		case SNRM:
			gdDLMSFrame = new byte[] {};
			break;
		case AARQ:
			gdDLMSFrame = new byte[] {};
			break;
		case ACTION_REQ:
			gdDLMSFrame = new byte[] {};
			break;
		case DISC:
			gdDLMSFrame = new byte[] {};
			break;
		default:
			break;
		}

		return gdDLMSFrame;
	}

	@Override
	public boolean decode(byte[] rawFrame, Procedure procedure, String command) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String toByteString() {
		return Hex.decode(gdDLMSFrame);
	}

	@Override
	public int getType() {
		return HdlcObjectType.getItem(controlType);
	}

	@Override
	public void setType(int type) {
		controlType = HdlcObjectType.getItem(type);
	}

	@Override
	public Object getResultData() {
		return resultData;
	}

	@Override
	public void setResultData(Object resultData) {
		this.resultData = resultData;
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getMeterId() {
		return meterId;
	}

	@Override
	public void setMeterId(String meterId) {
		this.meterId = meterId;
	}

	@Override
	public void setMeterRSCount(int[] rsCount) {
		if(meterRSCount == null){
			meterRSCount = rsCount;			
		}else{
			int[] temp = new int[4];
			temp[0] = (2 < meterRSCount.length ? meterRSCount[2] : 0);
			temp[1] = (2 < meterRSCount.length ? meterRSCount[3] : 0);
			temp[2] = rsCount[0];
			temp[3] = rsCount[1];
			
			meterRSCount = temp;
		}
	}

	@Override
	public int[] getMeterRSCount() {
		return meterRSCount;
	}

    @Override
    public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean decode(IoSession session, byte[] rawFrame, Procedure procedure) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isHDLCSegmented() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setHDLCSegmented(boolean isHDLCSegmented) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setHDLCFrameLength(int length) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public int getHDLCFrameLength() {
        // TODO Auto-generated method stub
        return 0;
    }
}
