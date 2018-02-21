/** 
 * @(#)Class7_1.java       1.0 04/10/19 *
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
 * ACRATIS SL7000 meter Class. <p> * 
 * Include LP Data Information. <p>
 */
public class Class7_1 {
	
	public final static int OFS_LPRECORDTIME  = 66;
	public final static int OFS_ACTIVESCALE   = 33;
	public final static int OFS_REACTIVESCALE = 47;
	
	private byte[] data;

	/**
	 * Contructor. <p>
	 * @param data - read data. <p>
	 */
	public Class7_1(byte[] data){
		this.data = data;
	}
	
	
	/**
	 * Get LP Start and End Record Time.<p>
	 * @return
	 */
	/*
	public byte[] parseStartTime() throws Exception {
		return DataFormat.select(data,OFS_LPRECORDTIME,12);
	}
	*/


	/**
	 * Get LP Scale Array.<p>
	 * @return
	 */
	public byte[] parseLPScale(){
		
		byte[] scale = new byte[2];
		scale[0] = data[OFS_ACTIVESCALE];
		scale[1] = data[OFS_REACTIVESCALE];
		
		return scale;
	}


	/**
	 * Get Active Power+ Scale.<p>
	 * @return
	 */
	public byte parseActiveScale(){
		return data[OFS_ACTIVESCALE];
	}
	
	
	/**
	 * Get Reactive Power+ Scale.<p>
	 * @return
	 */
	public byte parseReactiveScale(){
		return data[OFS_REACTIVESCALE];
	}

}
