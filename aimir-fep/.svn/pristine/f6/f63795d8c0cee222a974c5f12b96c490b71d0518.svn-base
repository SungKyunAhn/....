/** 
 * @(#)MT078.java       1.0 06/06/14 *
 * 
 * Security Log Table Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.SM110Table;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class MT078 implements java.io.Serializable {

	private static final long serialVersionUID = 4628934664909621506L;
	public static final int OFS_DT_LAST_CALIBRATED          = 0;
	public static final int OFS_CALIBRATOR_NAME             = 5;
	public static final int OFS_DT_LAST_PROGRAMMED          = 15;
	public static final int OFS_PROGRAMMER_NAME             = 20;
	public static final int OFS_NBR_TIMES_PROGRAMMED        = 30;
	public static final int OFS_NBR_DEMAND_RESETS           = 32;	
	public static final int OFS_DT_LAST_DEMAND_RESET        = 33;
	public static final int OFS_NBR_COMMUNICATIONS_SESSIONS = 38;
	public static final int OFS_DT_LAST_COMM_SESSION        = 40;
	public static final int OFS_NBR_BAD_PASSWORDS           = 45;
	public static final int OFS_NBR_RTP_ENTRIES             = 47;
	public static final int OFS_DT_LAST_RTP_ENTRY           = 49;	
	public static final int OFS_CUM_POWER_OUTAGE_SECS       = 54;
	public static final int OFS_NBR_POWER_OUTAGES           = 58;
	public static final int OFS_DT_LAST_POWER_OUTAGE        = 60;	
	public static final int OFS_NBR_EEPROM_WRITES           = 65;
	public static final int OFS_DT_LAST_TLC_UPDATE          = 68;
	public static final int OFS_TLC_NAME                    = 73;	
	public static final int OFS_DT_LAST_TRANS_INACC_ADJUST  = 83;
	public static final int OFS_TRANS_INACC_ADJUST_NAME     = 88;
	public static final int OFS_DT_LAST_TIME_CHANGE         = 98;
	public static final int OFS_RESERVED                    = 103;

	public static final int LEN_DT_LAST_CALIBRATED          = 5;
	public static final int LEN_CALIBRATOR_NAME             = 10;
	public static final int LEN_DT_LAST_PROGRAMMED          = 5;
	public static final int LEN_PROGRAMMER_NAME             = 10;
	public static final int LEN_NBR_TIMES_PROGRAMMED        = 2;
	public static final int LEN_NBR_DEMAND_RESETS           = 1;	
	public static final int LEN_DT_LAST_DEMAND_RESET        = 5;
	public static final int LEN_NBR_COMMUNICATIONS_SESSIONS = 2;
	public static final int LEN_DT_LAST_COMM_SESSION        = 5;
	public static final int LEN_NBR_BAD_PASSWORDS           = 2;
	public static final int LEN_NBR_RTP_ENTRIES             = 2;
	public static final int LEN_DT_LAST_RTP_ENTRY           = 5;	
	public static final int LEN_CUM_POWER_OUTAGE_SECS       = 4;
	public static final int LEN_NBR_POWER_OUTAGES           = 2;
	public static final int LEN_DT_LAST_POWER_OUTAGE        = 5;	
	public static final int LEN_NBR_EEPROM_WRITES           = 3;
	public static final int LEN_DT_LAST_TLC_UPDATE          = 5;
	public static final int LEN_TLC_NAME                    = 10;
	public static final int LEN_DT_LAST_TRANS_INACC_ADJUST  = 5;
	public static final int LEN_TRANS_INACC_ADJUST_NAME     = 10;
	public static final int LEN_DT_LAST_TIME_CHANGE         = 5;
	public static final int LEN_RESERVED                    = 10;
	
	private byte[] data;
    
    public MT078() {}
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public MT078(byte[] data) {
		this.data = data;
	}

	public long getCUM_POWER_OUTAGE_SECS() throws Exception {
		return DataFormat.hex2long(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,
					OFS_CUM_POWER_OUTAGE_SECS,
					LEN_CUM_POWER_OUTAGE_SECS)));
	}
	
	public int getNBR_POWER_OUTAGES() throws Exception {
		return DataFormat.hex2dec(
			DataFormat.LSB2MSB(
				DataFormat.select(
					data,
					OFS_NBR_POWER_OUTAGES,
					LEN_NBR_POWER_OUTAGES)));
	}
	
	public String getDT_LAST_POWER_OUTAGE() throws Exception {
		return getYyyymmddhhmm(
			data,OFS_DT_LAST_POWER_OUTAGE,LEN_DT_LAST_POWER_OUTAGE);
	}
	
	
	
	private byte[] parseYyyymmddhhmm(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[6];
		
		int blen = b.length;
		if(blen-offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		
		int currcen = (Integer.parseInt(DateTimeUtil.
                getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}

		datetime[0] = (byte)((year >> 8) & 0xff);
		datetime[1] = (byte)(year & 0xff);
		datetime[2] = (byte) MM;
		datetime[3] = (byte) dd;
		datetime[4] = (byte) hh;
		datetime[5] = (byte) mm;
		
		return datetime;
		
	}
	
	private String getYyyymmddhhmm(byte[] b, int offset, int len)
							throws Exception {
		
		int blen = b.length;
		if(blen-offset < 5)
			throw new Exception("YYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 5)
			throw new Exception("YYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int yy = DataFormat.hex2unsigned8(b[idx++]);
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();
		
		int currcen = (Integer.parseInt(DateTimeUtil.
		        getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}
		
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		
		return ret.toString();
			
	}

}
