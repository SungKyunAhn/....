/** 
 * @(#)ST022.java       1.0 06/12/14 *
 * 
 * Data Selection Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

/**
 * ex)
 * 00 04 0c ff ff 
 * 28 2c 34 ff ff 
 * 1f 
 * ff ff ff ff ff ff ff ff ff ff 
 * 00 00 01 01 02 02 03 03 04 04 
 */

/**
 * ex)
 * 00 0a 01 0c 0b 
 * 28 32 29 34 33 
 * 1f 
 * ff ff ff ff ff ff ff ff ff ff 
 * 00 00 01 01 02 02 03 03 04 04 
 */



/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST022 {
	
	public static final int OFS_SUMMATION_SELECT        = 0;
	public static final int OFS_DEMAND_SELECT           = 5;
	public static final int OFS_MIN_OR_MAX_FLAGS        = 10;
	public static final int OFS_COINCIDENT_SELECT       = 11;
	public static final int OFS_COINCIDENT_DEMAND_ASSOC = 21;
	
	public static final int LEN_SUMMATION_SELECT        = 5;
	public static final int LEN_DEMAND_SELECT           = 5;
	public static final int LEN_MIN_OR_MAX_FLAGS        = 1;
	public static final int LEN_COINCIDENT_SELECT       = 10;
	public static final int LEN_COINCIDENT_DEMAND_ASSOC = 10;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST022(byte[] data) {
		this.data = data;
	}
}
