/** 
 * @(#)Class3_1.java       1.0 04/10/19 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.sl7000Table;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * ACRATIS SL7000 meter Class. <p>
 * Include NON FATAL_ERROR Information. <p>
 */
public class Class3_1 {
	
	public final static int OFS_NFATAL_ERROR = 30;
	public final static int LEN_NFATAL_ERROR = 5;
	
	private byte[] data;
	
	
	/**
	 * Constructor . <p>
	 * @param data
	 */
	public Class3_1(byte[] data){
		this.data = data;	
	}

	
	/**
	 * Get Non Fatal Error Field.<p>
	 * @return
	 */
	public byte[] parseNFatalError() throws Exception {
		return DataFormat.select(data,OFS_NFATAL_ERROR,LEN_NFATAL_ERROR);
	}
	
}
