/**
 * (@)# BypassProcedure.java
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
public interface BypassProcedure {
	public abstract void encode(HdlcObjectType objectType, BypassProcedure procedure, Object initParam);

	public abstract void bypassWrite(IoSession session, byte[] frame);
}
