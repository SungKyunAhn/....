/** 
 * @(#)ST055.java       1.0 06/12/14 *
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
 
package com.aimir.fep.meter.parser.kV2cTable;

import com.aimir.fep.util.DataFormat;
import com.aimir.fep.util.Util;
import com.aimir.util.DateTimeUtil;

/**
 * 
 * ex) 06 06 1a 16 22 34 41 02 0f 
 */

/**
 * @author Park YeonKyoung yeonkyoung@hanmail.net
 */
public class ST055 {
	
	public static final int OFS_TM = 0;
	public static final int LEN_TM = 6;
    
    public static final int OFS_TIME_DATE_QUAL_BFLD = 7;
	
	private byte[] data;

	/**
	 * Constructor .<p>
	 * 
	 * @param data - read data (header,crch,crcl)
	 */
	public ST055(byte[] data) {
		this.data = data;
	}
    
    /**
     * 08 05 0e 15 07 01 
     * 4b 02 0f
     * 4b-> 01001011
     *  0 filler
     *  1 dst apply
     *  0 timezone apply
     *  0 gmt flag
     *  1 dst flag
     *  011 day of week (3 : wed)
     * @return
     */
    public int getDstApplyOn()
    {
        int val = data[OFS_TIME_DATE_QUAL_BFLD]&0xFF;
        if((val & 0x40) > 0){
            return 1;
        }else{
            return 0;
        }
    }
    
    public String getDstApplyOnName()
    {
        if(getDstApplyOn() == 0){
            return "Off";
        }else{
            return "On";
        }
    }
    
    public int getDstSeasonOn()
    {
        int val = data[OFS_TIME_DATE_QUAL_BFLD]&0xFF;
        if((val & 0x08) > 0){
            return 1;
        }else{
            return 0;
        }
    }
    
    public String getDstSeasonOnName()
    {
        if(getDstSeasonOn() == 0){
            return "Off";
        }else{
            return "On";
        }
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
    
    public long getTime() throws Exception {
        return Util.getMilliTimes(getDateTime());
    }	
	
	public int getTimeDiff() throws Exception {
		
		long systime = System.currentTimeMillis();
		long metertime = Util.getMilliTimes(getDateTime());
		
		return (int)((systime-metertime)/1000);
	}
}
