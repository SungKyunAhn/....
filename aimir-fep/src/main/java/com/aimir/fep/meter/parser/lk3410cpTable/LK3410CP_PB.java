/* 
 * @(#)LK3410CP_PB.java       1.0 07/11/08 *
 * 
 * Previous Billing.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lk3410cpTable;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class LK3410CP_PB extends BillingData{

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LK3410CP_PB(byte[] rawData) {
        super(rawData);
	}
    
}
