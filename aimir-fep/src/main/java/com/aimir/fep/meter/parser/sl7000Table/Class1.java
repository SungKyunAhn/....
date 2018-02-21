/** 
 * @(#)Class1.java       1.0 04/10/19 *
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
 * Include Meter Serial Information. <p>
 */
public class Class1 {

	public final static int OFS_SERIAL      = 20;	// offset meter serial number.
	public final static int LEN_SERIAL      = 8;	// length meter serial number.

	private byte[] data;

	/**
	 * Constructor 
	 * 
	 * @param data - read data
	 */
	public Class1(byte[] data) {
		this.data = data;
	}
	
	/**
	 * Get Meter Serial Number. <p>
	 * @return
	 */
	public byte[] parseSerial() throws Exception{		
		return DataFormat.select(data,OFS_SERIAL,LEN_SERIAL);
	}

}
