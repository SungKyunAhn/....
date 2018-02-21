/**
 * (@)# BypassCommandAction.java
 *
 * 2016. 6. 1.
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

import com.aimir.fep.protocol.nip.client.bypass.BypassResult;
import com.aimir.fep.protocol.nip.client.multisession.MultiSession;
import com.aimir.fep.protocol.nip.client.multisession.MultiSessionAdaptor;

/**
 * @author simhanger
 *
 */
public abstract class BypassCommandAction extends MultiSessionAdaptor {
	private static Logger logger = LoggerFactory.getLogger(BypassCommandAction.class);

	public abstract void execute(MultiSession session, String command) throws Exception;

	public abstract void executeBypass(MultiSession session, byte[] frame) throws Exception;

	public abstract BypassResult getBypassResult(MultiSession session) throws Exception;

}
