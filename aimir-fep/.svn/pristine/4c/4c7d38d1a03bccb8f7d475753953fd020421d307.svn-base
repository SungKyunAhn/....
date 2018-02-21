/** 
 * @(#)MT15.java       1.0 05/07/25 *
 * 
 * KE SCALE Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a1830rlnTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT15 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8715298340714668146L;
	public static final int OFS_KE_SCALE = 5;
	public static final int LEN_KE_SCALE = 1;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT15(byte[] data) {
		this.data = data;
	}

	public byte parseKEScale() throws Exception {
		return data[OFS_KE_SCALE];
	}

	public double getKEScale() throws Exception {
		return Math.pow(10,DataFormat.hex2signed8(data[OFS_KE_SCALE]));
	}
	
	public int getKEScaleP() throws Exception {
		return DataFormat.hex2signed8(data[OFS_KE_SCALE]);
	}

}
