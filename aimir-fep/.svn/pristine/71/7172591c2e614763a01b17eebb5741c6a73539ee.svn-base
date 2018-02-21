/** 
 * @(#)ST08.java       1.0 05/07/25 *
 * 
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
public class ST08 {
	
	private byte[] data;

	private final int OFS_PROC = 0;
	private final int OFS_SEQ_NBR = 2;
	private final int OFS_RESULT_CODE = 3;
	private final int OFS_RESP_DATA = 4;
	private final int OFS_OLDDATETIME = 4;
	private final int OFS_NEWDATETIME = 10;
	
	private final int LEN_PROC = 2;
	private final int LEN_SEQ_NBR = 1;
	private final int LEN_RESULT_CODE = 1;
	
	private final int LEN_OLDDATETIME = 6;
	private final int LEN_NEWDATETIME = 6;

	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST08(byte[] data) {
		this.data = data;
	}

	public void parseResultCode() throws Exception {
		
		if(data == null || data.length < 4)
			throw new Exception("Read Error -  ST8");
			
		byte ret = data[OFS_RESULT_CODE];
		
		switch(ret){
			case 0:
				//procedure completed
				break;
			case 1:
				throw new Exception("procedure accepted but not fully completed");
			case 2:
				throw new Exception("invalid parameter for known procedure");
			case 3:
				throw new Exception("procedure conflicts with current device setup");
			case 4:
				throw new Exception("timing constraint, procedure was ignored");
			case 5:
				throw new Exception("no authoization procedure ignored");
			case 6:
				throw new Exception("unrecognized procedure, procedure ignored");
			default : 
				throw new Exception("unknown error!");
		}
	}

	public String getOldTime() throws Exception {
		return getYyyymmddhhmmss(data,OFS_OLDDATETIME,LEN_OLDDATETIME);
	}
	
	public String getNewTime() throws Exception {
		return getYyyymmddhhmmss(data,OFS_NEWDATETIME,LEN_NEWDATETIME);
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
