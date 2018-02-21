/** 
 * @(#)Class3.java       1.0 04/10/19 *
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
package com.aimir.fep.meter.parser.sl7000Table;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 *
 * ACRATIS SL7000 meter Class. <p>
 * Include FATAL_ERROR. <p>
 */
public class Class3 {
	
	public final static int OFS_FATAL_ERROR  = 30;
	
	private byte[] data;
	
	
	/**
	 * Constructor . <p>
	 * @param data
	 */
	public Class3(byte[] data){
		this.data = data;	
	}

	/**
	 * Get Fatal Error Field.<p>
	 * @return
	 */
	public byte parseFatalError(){
		return data[OFS_FATAL_ERROR];
	}
	
}
