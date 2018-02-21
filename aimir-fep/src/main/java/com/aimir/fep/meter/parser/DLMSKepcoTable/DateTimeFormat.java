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
 
package com.aimir.fep.meter.parser.DLMSKepcoTable;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * 
 * for 6 or 7 byte data
 */

/**
 * @author Kang Soyi ksoyi@nuritelecom.com
 */

public class DateTimeFormat {
	
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
			 = getYyyymmddhhmmss(data);
    //    log.debug("yyyymmddhhmmss : "+ yyyymmddhhmm);
		return yyyymmddhhmm;
	}
	
	private String getYyyymmddhhmmss(byte[] b)
							throws Exception {
		
		int len = b.length;
		
		if(len <6 && len>9)
			throw new Exception("YYYYMMDDHHMMSS LEN ERROR : "+len);
		
		int idx = 0;
		if(len==9)
			idx ++;
		
		int year = DataFormat.hex2unsigned16(DataFormat.select(b, idx, 2));
        idx =idx+2;
		int mm = DataFormat.hex2unsigned8(b[idx++]);
		int dd = DataFormat.hex2unsigned8(b[idx++]);
		int hh = DataFormat.hex2unsigned8(b[idx++]);
		int MM = DataFormat.hex2unsigned8(b[idx++]);
		int ss = 0;
		if(len>6)
			ss = DataFormat.hex2unsigned8(b[idx++]);

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
