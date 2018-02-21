/** 
 * @(#)ST51.java       1.0 06/09/25 *
 * 
 * Calendar Class.
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
 * ST_051_ACT_TIME_TOU
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST51 implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7341503108769894129L;
	/**
	 * TIME_FUNC_FLAG1: 31 
	 * TIME_FUNC_FLAG2: 1e 
	 * CALENDAR_FUNC: 2c
	 * NBR_NONE_RECURR_DATES: ff 
	 * NBR_RECURR_DATES: 23 
	 * NBR_TIER_SWITCHES: 84 00 
	 * CALENDAR_SIZE: 60 05
	 */
	public static final int OFS_TIME_FUNC_FLAG1       = 0;
	public static final int OFS_TIME_FUNC_FLAG2       = 1;
	public static final int OFS_CALENDAR_FUNC         = 2;
	public static final int OFS_NBR_NONE_RECURR_DATES = 3;
	public static final int OFS_NBR_RECURR_DATES      = 4;
	public static final int OFS_NBR_TIER_SWITCHES     = 5;
	public static final int OFS_CALENDAR_SIZE         = 7;
	
	public static final int LEN_TIME_FUNC_FLAG1       = 1;
	public static final int LEN_TIME_FUNC_FLAG2       = 1;
	public static final int LEN_CALENDAR_FUNC         = 1;
	public static final int LEN_NBR_NONE_RECURR_DATES = 1;
	public static final int LEN_NBR_RECURR_DATES      = 1;
	public static final int LEN_NBR_TIER_SWITCHES     = 2;
	public static final int LEN_CALENDAR_SIZE         = 2;
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST51(byte[] data) {
		this.data = data;
	}	
	
	public int getNBR_SEASONS(){
		return DataFormat.hex2unsigned8(data[OFS_CALENDAR_FUNC])&0x0F;
	}
	
	public int getNBR_SPECIAL_SCHED(){
		return DataFormat.hex2unsigned8(data[OFS_CALENDAR_FUNC])>> 4;
	}
	
	public int getLEN_NBR_NONE_RECURR_DATES() throws Exception {
		return DataFormat.hex2unsigned8(data[OFS_NBR_NONE_RECURR_DATES]);
	}
	
	public int getNBR_RECURR_DATES() throws Exception {
		return DataFormat.hex2unsigned8(data[OFS_NBR_RECURR_DATES]);
	}
	
	public int getNBR_TIER_SWITCHES() throws Exception {
		return DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_NBR_TIER_SWITCHES,LEN_NBR_TIER_SWITCHES)));
	}
	
	public int getCALENDAR_SIZE() throws Exception {
		return DataFormat.hex2unsigned16(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,OFS_CALENDAR_SIZE,LEN_CALENDAR_SIZE)));
	}

}
