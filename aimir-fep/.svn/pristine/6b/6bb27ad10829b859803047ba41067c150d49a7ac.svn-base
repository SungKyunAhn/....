/** 
 * @(#)DateTimeFormat.java       1.0 07/11/12 *
 * 
 * Meter Time Class.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.lk3410cpTable;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * 
 * ex) 06 06 1a 16 22 34 41 02 0f 
 */

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class DateTimeFormat {
	
	public static final int OFS_TM = 0;
	public static final int LEN_TM = 7;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DateTimeFormat(byte[] data) {
		this.data = data;
	}
	
	public String getDateTime() throws Exception {

		String yyyymmddhhmm 		
			 = getYyyymmddhhmmss(data,OFS_TM,LEN_TM);
    //    log.debug("yyyymmddhhmmss : "+ yyyymmddhhmm);
		return yyyymmddhhmm;
	}
	
	private String getYyyymmddhhmmss(byte[] b, int offset, int len)
							throws Exception {
		
		int blen = b.length;
		if(blen-offset < 7)
			throw new Exception("YYYYMMDDHHMMSS FORMAT ERROR : "+(blen-offset));
		if(len != 7)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = offset;
		
		int year = DataFormat.hex2unsigned16(DataFormat.LSB2MSB(DataFormat.select(b, 0, 2)));
        idx =idx+2;
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = DataFormat.hex2unsigned8(b[idx++]);

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		
		return ret.toString();
			
	}
	
}
