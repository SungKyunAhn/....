/**
 * (@)# HdlcSNRMProcedure.java
 *
 * 2016. 4. 28.
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
package com.aimir.fep.bypass.decofactory.procedure;

import org.apache.mina.core.session.IoSession;

import com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcObjectType;

/**
 * @author simhanger
 *
 */
public class HdlcSNRMProcedure implements BypassProcedure {
	private static HdlcSNRMProcedure singlton;

	/* (non-Javadoc)
	 * @see com.aimir.fep.bypass.decofactory.procedure.BypassProcedure#encode(com.aimir.fep.bypass.decofactory.consts.HdlcConstants.HdlcObjectType, com.aimir.fep.bypass.decofactory.procedure.BypassProcedure, java.lang.Object)
	 */
	@Override
	public void encode(HdlcObjectType objectType, BypassProcedure procedure, Object initParam) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aimir.fep.bypass.decofactory.procedure.BypassProcedure#bypassWrite(org.apache.mina.core.session.IoSession, byte[])
	 */
	@Override
	public void bypassWrite(IoSession session, byte[] frame) {
		// TODO Auto-generated method stub

	}

	public static BypassProcedure getInstance() {
		if(singlton == null){
			singlton = new HdlcSNRMProcedure();
		}
		return singlton;
	}

}
