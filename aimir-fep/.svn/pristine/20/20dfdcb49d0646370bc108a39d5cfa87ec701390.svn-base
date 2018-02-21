/** 
 * @(#)ST61.java       1.0 05/07/25 *
 * 
 * Actual Load Profile Table.
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
public class ST61 implements java.io.Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3500921991383415184L;
	private final int OFS_LP_MEMORY_LEN      = 0;
	private final int OFS_LP_FLAGS           = 4;
	private final int OFS_LP_FORMATS         = 6;
	private final int OFS_NBR_BLKS_SET1      = 7;
	private final int OFS_NBR_BLKS_INTS_SET1 = 9;
	private final int OFS_NBR_CHNS_SET1      = 11;
	private final int OFS_INT_TIME_SET1      = 12;
	
	private final int LEN_LP_MEMORY_LEN      = 4;
	private final int LEN_LP_FLAGS           = 2;
	private final int LEN_LP_FORMATS         = 1;
	private final int LEN_NBR_BLKS_SET1      = 2;
	private final int LEN_NBR_BLKS_INTS_SET1 = 2;
	private final int LEN_NBR_CHNS_SET1      = 1;
	private final int LEN_INT_TIME_SET1      = 1;//LP Interval time
	
	private byte[] data;

    private Log logger = LogFactory.getLog(getClass());
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST61(byte[] data) {
		this.data = data;
	}

	/**
	 * LP Data Block count
	 */
	public int getNBR_BLKS_SET1() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NBR_BLKS_SET1,LEN_NBR_BLKS_SET1)));
	}
	
	/**
	 * LP Data count(interval) of 1 Block (
	 */
	public int getNBR_BLKS_INTS_SET1() throws Exception {
		return DataFormat.hex2unsigned16(
				DataFormat.LSB2MSB(
					DataFormat.select(
						data,OFS_NBR_BLKS_INTS_SET1,LEN_NBR_BLKS_INTS_SET1)));
	}


	/**
	 * LP Channel Size
	 * Default 2 (1~20)
	 * @return
	 */
	public int getNBR_CHNS_SET1() {
		return DataFormat.hex2unsigned8(data[OFS_NBR_CHNS_SET1]);
	}
	
	/**
	 * LP Interval Time
	 * Default 15
	 * (0,1,2,3,4,5,6,10,12,15,20,30,60)
	 * @return
	 */
	public int getINT_TIME_SET1() {
		return DataFormat.hex2unsigned8(data[OFS_INT_TIME_SET1]);
	}
	
	
	public byte parseINT_TIME_SET1() {
		return data[OFS_INT_TIME_SET1];
	}

}
