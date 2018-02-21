/** 
 * @(#)ST071.java       1.0 07/01/29 *
 * 
 * Event Log Actual Dimension Log Table.
 * Copyright (c) 2007-2008 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.kV2cTable;

import com.aimir.fep.util.DataFormat;

/**
 * @author Park YeonKyoung goodjob@nuritelecom.com
 */
public class ST071 {

	private final int OFS_LOG_FLAG_BFLD     = 0;
	private final int OFS_NBR_STD_EVENTS    = 1;
	private final int OFS_NBR_MFG_EVENTS    = 2;
	private final int OFS_HIST_DATA_LEN     = 3;
	private final int OFS_EVENT_DATA_LEN    = 4;
	private final int OFS_NBR_HIST_ENTRIES  = 5;
	private final int OFS_NBR_EVENT_ENTRIES = 7;
	
	private final int LEN_NBR_EVENT_ENTRIES = 2;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST071(byte[] data) {
		this.data = data;
	}
	
	
	public int getNBR_EVENT_ENTRIES() 
					throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NBR_EVENT_ENTRIES,LEN_NBR_EVENT_ENTRIES)));
	}

}
