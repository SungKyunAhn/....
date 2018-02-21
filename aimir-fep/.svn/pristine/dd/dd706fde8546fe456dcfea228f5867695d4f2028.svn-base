/** 
 * @(#)SelfReadingDateTime.java       1.0 07/11/14 *
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
 
package com.aimir.fep.meter.parser.lgrw3410Table;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author Kang SoYi ksoyi@nuritelecom.com
 */
public class SelfReadingDateTime {
	
    public static final int OFS_YYYY  = 0;
    public static final int OFS_MM  = 2;
    public static final int OFS_DD  = 3;
    public static final int OFS_HH  = 4;
    public static final int OFS_MI  = 5;
    public static final int OFS_SS  = 6;
    public static final int OFS_CNT  = 8;
    
    public static final int LEN_YYYY  = 2;
    public static final int LEN_CNT  = 2;
    
    public static final int LEN_SELF_READING_DATETIME=12;
    
	private byte[] data;
    private static Log log = LogFactory.getLog(SelfReadingDateTime.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data ()
	 */
	public SelfReadingDateTime(byte[] data) {
		this.data = data;
	}
	
    /**
     * SelfReadingDateTime
     */
	public String getSelfReadingDateTime() throws Exception  {
		String lastDateTime ="";
		int cnt =0;
		for(int i=0; i<5; i++){
			byte[] data1 = DataFormat.select(data,i*(LEN_SELF_READING_DATETIME), LEN_SELF_READING_DATETIME);
			int nowCnt = getCnt(data1); 
			if( cnt< nowCnt){
				cnt = nowCnt;
				lastDateTime = getDateTime(data1);
			}
		}
		return lastDateTime;
	}
	
    public String getDateTime(byte[] date1) throws Exception  {
        int year = DataFormat.hex2dec(DataFormat.LSB2MSB(DataFormat.select(date1,OFS_YYYY,LEN_YYYY)));
        int month = DataFormat.hex2dec(DataFormat.select(date1,OFS_MM,1));
        int date = DataFormat.hex2dec(DataFormat.select(date1,OFS_DD,1));
        int hour = DataFormat.hex2dec(DataFormat.select(date1,OFS_HH,1));
        int min = DataFormat.hex2dec(DataFormat.select(date1,OFS_MI,1));
        int sec = DataFormat.hex2dec(DataFormat.select(date1,OFS_SS,1));

        StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(date),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));
        return ret.toString();
    }
	    
    public int getCnt(byte[] date1) throws Exception  {
    	return (int)DataFormat.hex2dec(DataFormat.select(date1,OFS_CNT, LEN_CNT));
    }
    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("SelfReadingDateTime DATA[");        
            sb.append("(SelfReadingDateTime=").append(""+getSelfReadingDateTime()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LPIntervalTime TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
