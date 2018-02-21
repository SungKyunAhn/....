/**
 * (@)# NestedNIDecorator.java
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
package com.aimir.fep.bypass.decofactory.decorator;

import java.util.HashMap;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcObjectType;
import com.aimir.fep.bypass.decofactory.decoframe.INestedFrame;
import com.aimir.fep.bypass.decofactory.protocolfactory.BypassFrameFactory.Procedure;

/**
 * @author simhanger
 *
 */
public class NestedNIDecorator extends NestFrameDecorator {

	/**
	 * @param nestedFrame
	 */
	public NestedNIDecorator(INestedFrame nestedFrame) {
		super(nestedFrame);
	}
	
	@Override
	public boolean decode(IoSession session, byte[] frame, Procedure procedure) {
		// TODO Auto-generated method stub
		return super.decode(session, frame, procedure);
	}

	@Override
	public boolean decode(byte[] frame, Procedure procedure, String commmand) {
		// TODO Auto-generated method stub
		return super.decode(frame, procedure, commmand);
	}

    @Override
	public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param) {
		// TODO Auto-generated method stub
		return super.encode(hdlcType, procedure, param);
	}
    
	@Override
	public byte[] encode(HdlcObjectType hdlcType, Procedure procedure, HashMap<String, Object> param, String command) {
		// TODO Auto-generated method stub
		return super.encode(hdlcType, procedure, param, command);
	}

	@Override
	public Object getResultData() {
		// TODO Auto-generated method stub
		return super.getResultData();
	}

	@Override
	public int getType() {
		// TODO Auto-generated method stub
		return super.getType();
	}

	@Override
	public void setResultData(Object resultData) {
		// TODO Auto-generated method stub
		super.setResultData(resultData);
	}

	@Override
	public void setType(int type) {
		// TODO Auto-generated method stub
		super.setType(type);
	}

	@Override
	public String toByteString() {
		// TODO Auto-generated method stub
		return super.toByteString();
	}

	@Override
	public Object customDecode(Procedure procedure, byte[] data) throws Exception {
		return super.customDecode(procedure, data);
	}
}
