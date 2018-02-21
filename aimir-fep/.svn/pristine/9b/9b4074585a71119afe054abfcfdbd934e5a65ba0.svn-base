/** 
 * @(#)MeterErrorFlag.java       1.0 09/03/17 *
 * 
 * Actual Dimension Register Table.
 * Copyright (c) 2006-2007 NuriTelecom, Inc.
 * All rights reserved. * 
 * This software is the confidential and proprietary information of 
 * Nuritelcom, Inc. ("Confidential Information").  You shall not 
 * disclose such Confidential Information and shall use it only in 
 * accordance with the terms of the license agreement you entered into 
 * with Nuritelecom. 
 */
 
package com.aimir.fep.meter.parser.actarisSCE8711Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang, SoYi ksoyi@nuritelecom.com
 */
public class DLMSDateTime {
	
    public static final int OFS_DEVIATION_HIGHBYTE = 9;
    public static final int LEN_DEVIATION_HIGHBYTE = 2;
    
	public byte[] date = null;
	
    private Log log = LogFactory.getLog(DLMSDateTime.class);
	
    
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public DLMSDateTime(byte[] date) throws Exception{
		this.date = date;
	//	getDateTime();
	}
	
	public String getDateTime()throws Exception {
    	int blen = date.length;
		if(blen <5)
			throw new Exception("YYYYMMDD LEN ERROR : "+blen);
		
		int idx = 0;
		
		int year = DataFormat.hex2unsigned16(DataFormat.select(date, 0, 2));
        idx =idx+2;
		int mm = DataFormat.hex2unsigned8(date[idx++]);
		int dd = DataFormat.hex2unsigned8(date[idx++]);		

		StringBuffer ret = new StringBuffer();
				
		ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
		ret.append(Util.frontAppendNStr('0',Integer.toString(mm),2));
		ret.append(Util.frontAppendNStr('0',Integer.toString(dd),2));
		
		int hh = 0;
		int MM = 0;
		int ss =0;
		idx ++;
		if(blen>5){
			
			if(date[idx++]==(byte)0xff)
				hh=0;
			else
				hh = DataFormat.hex2unsigned8(date[idx]);
			
			if(date[idx++]==(byte)0xff)
				MM=0;
			else
				MM = DataFormat.hex2unsigned8(date[idx]);
			
			if(date[idx++]==(byte)0xff)
				ss=0;
			else
				ss = DataFormat.hex2unsigned8(date[idx]);

			ret.append(Util.frontAppendNStr('0',Integer.toString(hh),2));
			ret.append(Util.frontAppendNStr('0',Integer.toString(MM),2));
			ret.append(Util.frontAppendNStr('0',Integer.toString(ss),2));
		}
		return ret.toString();
    }

	public boolean getDaylightSavingActive(){
		if((date[date.length-1] & 0x80)>0)
			return true;

		return false;
	}
	
	public int getDeviationHighByte() throws Exception{
		
		int adjMin = DataFormat.hex2signed16(DataFormat.select(date, OFS_DEVIATION_HIGHBYTE, LEN_DEVIATION_HIGHBYTE));
		
		if(adjMin>= -720 && adjMin<=720)
			return adjMin;
		else
			throw new Exception("DEVIATION_HIGHBYTE not specified or error : "+ adjMin);
		
	}

    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("MeterErrorFlag DATA[");        

            sb.append("]\n");
        }catch(Exception e){
            log.warn("DateTime TO STRING ERR=>"+e.getMessage());
        }
        return sb.toString();
    }
}
