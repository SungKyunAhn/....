/** 
 * @(#)ST71.java       1.0 05/07/25 *
 * 
 * Event Log Actual Dimension Log Table.
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
public class ST71 implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8475384106791490819L;
	private final int OFS_LOG_FLAG_BFLD     = 0;
	private final int OFS_NBR_STD_EVENTS    = 1;
	private final int OFS_NBR_MFG_EVENTS    = 2;
	private final int OFS_HIST_DATA_LEN     = 3;
	private final int OFS_EVENT_DATA_LEN    = 4;
	private final int OFS_NBR_HIST_ENTRIES  = 5;
	private final int OFS_NBR_EVENT_ENTRIES = 7;
	
	private final int LEN_NBR_EVENT_ENTRIES = 2;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST71(byte[] data) {
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
