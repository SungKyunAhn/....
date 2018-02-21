/** 
 * @(#)LPIntervalTime.java       1.0 2008/08/18 *
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
 
package com.aimir.fep.meter.parser.Mk6NTable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;

/**
 * @author kaze kaze@nuritelecom.com
 */
public class LPIntervalTime implements java.io.Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 4864511194628885720L;
	public static final int LP_TIME_YEAR = 0xFFF00000;
    public static final int LP_TIME_MON  = 0x000F0000;
    public static final int LP_TIME_DATE = 0x0000F800;
    public static final int LP_TIME_HOUR = 0x000007C0;
    public static final int LP_TIME_MIN  = 0x0000003F;
           
	private byte[] data;
    private static Log log = LogFactory.getLog(LPIntervalTime.class);
	
	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public LPIntervalTime(byte[] data) {
		this.data = data;
	}
	
    /**
     * LINE_MISSING
     */
    public String getIntervalTime() throws Exception  {
        int dataValue = (int)DataFormat.hex2dec(data);
        int year =(int)((dataValue&LP_TIME_YEAR) >> 20);
        int month = (int)((dataValue&LP_TIME_MON) >> 16);
        int date = (int)((dataValue&LP_TIME_DATE) >> 11);
        int hour = (int)((dataValue&LP_TIME_HOUR) >> 6);
        int min = (int)(dataValue&LP_TIME_MIN);

        StringBuffer ret = new StringBuffer();
        
        ret.append(Util.frontAppendNStr('0',Integer.toString(year),4));
        ret.append(Util.frontAppendNStr('0',Integer.toString(month),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(date),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(hour),2));
        ret.append(Util.frontAppendNStr('0',Integer.toString(min),2));
    //    ret.append(Util.frontAppendNStr('0',Integer.toString(sec),2));
        return ret.toString();
    }
	    
    public String toString()
    {
        StringBuffer sb = new StringBuffer();
        try{
            sb.append("LPIntervalTime DATA[");        
            sb.append("(IntervalTime=").append(""+getIntervalTime()).append(')');
            sb.append("]\n");
        }catch(Exception e){
            log.warn("LPIntervalTime TO STRING ERR=>"+e.getMessage());
        }

        return sb.toString();
    }
}
