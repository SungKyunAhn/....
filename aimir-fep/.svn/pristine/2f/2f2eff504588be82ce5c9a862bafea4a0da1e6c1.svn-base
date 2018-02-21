/** 
 * @(#)MT03.java       1.0 06/01/06 *
 * 
 * Meter Status Class.
 * Copyright (c) 2004-2005 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.a3rlnqTable;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;


/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT03 {
	
	private final int OFS_CONFIG_ERROR            = 0;
	private final int OFS_TBL_CRC_CURR_ERROR      = 7;
	private final int OFS_TBL_CRC_LATC_ERROR      = 15;
	private final int OFS_LATCHED_RESOURCE_ERROR  = 23;
	private final int OFS_LATCHED_ERROR           = 24;
	private final int OFS_LATCHED_WARN            = 26;
	private final int OFS_LATCHED_PQM_WARN        = 29;
	private final int OFS_OLD_TIME                = 33;
	private final int OFS_NEW_TIME                = 39;
	private final int OFS_COMM_STATUS             = 45;
	private final int OFS_DATETIME_LAST           = 67;
	private final int OFS_NUM_LOGONS              = 73;
	private final int OFS_NUM_WRITE_SESSION       = 75;
	private final int OFS_NUM_SECUR_FAILURES      = 77;
	private final int OFS_NBR_MANUAL_DEMANDRESET  = 76;
	private final int OFS_DEMANDRESET_MECH        = 78;
	private final int OFS_DAYS_SINCE_DEMAND_RESET = 79;
	private final int OFS_DAYS_SINCE_PULSE        = 80;
	private final int OFS_POWERFAILTIME           = 81;
	private final int OFS_POWERUPTIME             = 87;
	private final int OFS_CUM_POWEROUTAGE         = 93;
	private final int OFS_CUM_POWEROUTAGE_TIME    = 94;

	private final int LEN_CONFIG_ERROR            = 7;
	private final int LEN_TBL_CRC_CURR_ERROR      = 8;
	private final int LEN_TBL_CRC_LATC_ERROR      = 8;
	private final int LEN_LATCHED_RESOURCE_ERROR  = 1;
	private final int LEN_LATCHED_ERROR           = 2;
	private final int LEN_LATCHED_WARN            = 3;
	private final int LEN_LATCHED_PQM_WARN        = 4;
	private final int LEN_OLD_TIME                = 6;
	private final int LEN_NEW_TIME                = 6;
	private final int LEN_COMM_STATUS             = 22;
	private final int LEN_DATETIME_LAST           = 6;
	private final int LEN_NUM_LOGONS              = 2;
	private final int LEN_NUM_WRITE_SESSION       = 2;
	private final int LEN_NUM_SECUR_FAILURES      = 1;
	private final int LEN_NBR_MANUAL_DEMANDRESET  = 2;
	private final int LEN_DEMANDRESET_MECH        = 1;
	private final int LEN_DAYS_SINCE_DEMAND_RESET = 1;
	private final int LEN_DAYS_SINCE_PULSE        = 1;
	private final int LEN_POWERFAILTIME           = 6;
	private final int LEN_POWERUPTIME             = 6;
	private final int LEN_CUM_POWEROUTAGE         = 1;
	private final int LEN_CUM_POWEROUTAGE_TIME    = 4;
	
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * @param data - read data (header,crch,crcl)
	 */
	public MT03(byte[] data) {
		this.data = data;
	}
	
	public String getOldTime() throws Exception {
		return getYyyymmddhhmmss(data,OFS_OLD_TIME,LEN_OLD_TIME);
	}
	
	public String getNewTime() throws Exception {
		return getYyyymmddhhmmss(data,OFS_NEW_TIME,LEN_NEW_TIME);
	}
	
	
	private String getYyyymmddhhmmss(byte[] b, int offset, int len)
							throws Exception {
		
		int blen = b.length;
		if(blen-offset < 6)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 6)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
			
	}

}
