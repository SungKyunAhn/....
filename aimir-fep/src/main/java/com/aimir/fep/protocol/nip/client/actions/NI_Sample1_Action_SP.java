/**
 * (@)# NI_Sample1_Action_SP.java
 *
 * 2016. 10. 6.
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
package com.aimir.fep.protocol.nip.client.actions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.frame.GeneralFrame;
import com.aimir.util.DateTimeUtil;

/**
 * @author simhanger
 *
 */
public class NI_Sample1_Action_SP extends NICommandAction {
	private static Logger logger = LoggerFactory.getLogger(NI_Sample1_Action_SP.class);
	private String actionTitle = "NI_Sample1_Action_SP";

	public NI_Sample1_Action_SP() {
		actionTitle += "_" + DateTimeUtil.getCurrentDateTimeByFormat(null);
		logger.debug("### Action Title = {}", actionTitle);
	}

	@Override
	public String getActionTitle() {
		return actionTitle;
	}
	
	/* (non-Javadoc)
	 * @see com.aimir.fep.protocol.nip.client.actions.NICommandAction#executeStart(com.aimir.fep.protocol.nip.client.bypass.MultiSession, com.aimir.fep.protocol.nip.frame.GeneralFrame)
	 */
	@Override
	public Object executeStart(MultiSession session, GeneralFrame generalFrame) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aimir.fep.protocol.nip.client.actions.NICommandAction#executeTransaction(com.aimir.fep.protocol.nip.client.bypass.MultiSession, com.aimir.fep.protocol.nip.frame.GeneralFrame)
	 */
	@Override
	public Object executeTransaction(MultiSession session, GeneralFrame generalFrame) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.aimir.fep.protocol.nip.client.actions.NICommandAction#executeStop(com.aimir.fep.protocol.nip.client.bypass.MultiSession)
	 */
	@Override
	public void executeStop(MultiSession session) throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.aimir.fep.protocol.nip.client.actions.NICommandAction#executeAck(com.aimir.fep.protocol.nip.client.bypass.MultiSession, com.aimir.fep.protocol.nip.frame.GeneralFrame)
	 */
	@Override
	public void executeAck(MultiSession session, GeneralFrame generalFrame) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public void executeResponse(MultiSession session, GeneralFrame generalFrame) throws Exception {
		// TODO Auto-generated method stub
		
	}



}
