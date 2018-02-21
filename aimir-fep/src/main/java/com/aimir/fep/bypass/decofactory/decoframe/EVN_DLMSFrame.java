/**
 * (@)# EVN_DLMSFrame.java
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
 * @author kh.yoon
 *
 */
public class EVN_DLMSFrame implements INestedFrame {
	private static Logger logger = LoggerFactory.getLogger(EVN_DLMSFrame.class);

	private byte[] evnDLMSFrame = null;
	private HdlcObjectType controlType;
	public Object resultData;
	private boolean isHDLCSegmented;
	private int hdlcFrameLength;

	@Override
	public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param) {
		logger.debug("## Excute EVN_DLMSFrame Encoding [" + hdlcType.name() + "]");

		switch (hdlcType) {
		case SNRM:
			evnDLMSFrame = new byte[] {};
			break;
		case AARQ:
			evnDLMSFrame = new byte[] {};
			break;
		case ACTION_REQ:
			evnDLMSFrame = new byte[] {};
			break;
		case GET_REQ:
			evnDLMSFrame = new byte[] {};
			break;	
		case DISC:
			evnDLMSFrame = new byte[] {};
			break;
		case RR:
			evnDLMSFrame = new byte[] {};
			break;
		default:
			break;
		}

		return evnDLMSFrame;
	}


	@Override
	public boolean decode(IoSession session, byte[] rawFrame, Procedure procedure) {		
		return false;
	}
	
	@Override
	public String toByteString() {
		return Hex.decode(evnDLMSFrame);
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
	public boolean isHDLCSegmented() {
		return isHDLCSegmented;
	}

	@Override
	public void setHDLCSegmented(boolean isHDLCSegmented) {
		this.isHDLCSegmented = isHDLCSegmented;
	}

	@Override
	public void setHDLCFrameLength(int length) {
		this.hdlcFrameLength = length;
	}

	@Override
	public int getHDLCFrameLength() {
		return hdlcFrameLength;
	}


    @Override
    public byte[] encode(HdlcObjectType getReq, Procedure procedure, HashMap<String, Object> param, String command) {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public boolean decode(byte[] rawFrame, Procedure procedure, String command) {
        // TODO Auto-generated method stub
        return false;
    }


    @Override
    public String getMeterId() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public void setMeterId(String meterId) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void setMeterRSCount(int[] rsCount) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public int[] getMeterRSCount() {
        // TODO Auto-generated method stub
        return null;
    }


    @Override
    public Object customDecode(Procedure procedure, byte[] data) {
        // TODO Auto-generated method stub
        return null;
    }

}
