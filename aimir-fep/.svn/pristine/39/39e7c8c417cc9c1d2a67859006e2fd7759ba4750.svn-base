/** 
 * @(#)ST11.java       1.0 05/07/25 *
 * 
 * Actual Sources Limiting Table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
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
 * ex)
 *                       : 24 3e 05 02 3e 
 * NBR_CONSTANTS_ENTRIES : 16 
 *                       : 02 3e 
 */
/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST11 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1400127477596929016L;
	public static final int OFS_NBR_CONSTANTS_ENTRIES = 5;
	public static final int LEN_NBR_CONSTANTS_ENTRIES = 1;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST11(byte[] data) {
		this.data = data;
	}

	public int getNBR_CONSTANTS_ENTRIES() throws Exception {
		return DataFormat.hex2dec(
			data,OFS_NBR_CONSTANTS_ENTRIES,LEN_NBR_CONSTANTS_ENTRIES);
	}

}
