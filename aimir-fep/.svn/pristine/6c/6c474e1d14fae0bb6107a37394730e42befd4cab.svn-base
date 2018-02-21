/** 
 * @(#)ST55.java       1.0 05/07/25 *
 * 
 * Meter Time Class.
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
public class ST55 {
	
	public static final int OFS_TM = 0;
	public static final int LEN_TM = 6;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST55(byte[] data) {
		this.data = data;
	}

	public byte[] parseDateTime() throws Exception {
		return parseYyyymmddhhmm(data,OFS_TM,LEN_TM);		
	}
	
	public String getDateTime() throws Exception {

		String yyyymmddhhmm 		
			 = getYymmddhhmmss(data,OFS_TM,LEN_TM);
		return yyyymmddhhmm;
	}
	
	public String getYyyymmdd() throws Exception {

		String yyyymmddhhmm 		
			 = getYymmddhhmmss(data,OFS_TM,LEN_TM);
		return yyyymmddhhmm.substring(0,8);
	}
	
	
	private byte[] parseYyyymmddhhmm(byte[] b, int offset, int len)
						throws Exception {

		byte[] datetime = new byte[7];
		
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
		
		int currcen = (Integer.parseInt(DateTimeUtil
                .getCurrentDateTimeByFormat("yyyy"))/100)*100;
	
		int year   = yy;
		if(year != 0){
			year = yy + currcen;
		}

		datetime[0] = (byte)((year >> 8) & 0xff);
		datetime[1] = (byte)(year & 0xff);
		datetime[2] = (byte) mm;
		datetime[3] = (byte) dd;
		datetime[4] = (byte) hh;
		datetime[5] = (byte) MM;
		datetime[6] = (byte) ss;
		
		return datetime;
		
	}
	
	
	private String getYymmddhhmmss(byte[] b, int offset, int len)
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
	
	
	/**
	 * Get Meter Time Difference 
	 * Between Server Time and Meter Time
	 * @return secs
	 * @throws Exception
	 */
	public int getTimeDiff(long delaytime) throws Exception {
		
		long systime = System.currentTimeMillis()-delaytime;
		long metertime = Util.getMilliTimes(getDateTime());
		
		return (int)((systime-metertime)/1000);
	}
	
	
	public int getTimeDiff() throws Exception {
		
		long systime = System.currentTimeMillis();
		long metertime = Util.getMilliTimes(getDateTime());
		
		return (int)((systime-metertime)/1000);
	}
}
